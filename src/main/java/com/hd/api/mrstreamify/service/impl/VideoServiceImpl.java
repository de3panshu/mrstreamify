package com.hd.api.mrstreamify.service.impl;

import com.hd.api.mrstreamify.entity.ChunkUploadStatus;
import com.hd.api.mrstreamify.entity.Video;
import com.hd.api.mrstreamify.exception.ChunkAlreadyUploadedException;
import com.hd.api.mrstreamify.repo.VideoRepo;
import com.hd.api.mrstreamify.service.ChunkUploadStatusService;
import com.hd.api.mrstreamify.service.VideoService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class VideoServiceImpl implements VideoService {
    private static final Logger logger = LogManager.getLogger(VideoServiceImpl.class);

    @Value("${app.saved.dir}")
    private String SAVED_VIDEO_DIR;
    @Value("${app.temp.dir}")
    private String TEMP_VIDEO_DIR;

    private final String CHUNK_COUNT_SEPARATOR = "__";

    @Autowired
    private VideoRepo videoRepo;

    @Autowired
    private ChunkUploadStatusService chunkUploadStatus;
    @Override
    public Optional<Video> saveVideo(MultipartFile videoFile) {
        String videoTitle = StringUtils.cleanPath(Objects.requireNonNull(videoFile.getOriginalFilename()));

        Video video = Video.builder()
                .title(videoTitle)
                .build();
        video = videoRepo.save(video);


        //getting the extension
        String fileExtension = "";
        int indexOfLastDot = videoTitle.lastIndexOf('.');
        if (indexOfLastDot > -1) {
            fileExtension = videoTitle.substring(indexOfLastDot);
        }
        Path videoDirPath = createSavedDirIfNotExists(SAVED_VIDEO_DIR);
        //saving the video
        String newFileName = video.getVideoId().toString() + fileExtension;
        Path savingPath = videoDirPath.resolve(newFileName);

        try {
            Files.copy(videoFile.getInputStream(), savingPath);
        } catch (IOException e) {
            videoRepo.delete(video); //deleting the saved the video record.
            logger.debug(String.format("File(%s) saving failed, deleting the db record",video.getTitle()));
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return Optional.of(video);
    }


    @Override
    public Optional<Video> saveVideoChunk(MultipartFile videoFile, UUID videoId, int chunkIndex, int totalChunks) {
        logger.info("++++ saving chuck-{}/{}",chunkIndex+1,totalChunks);
        boolean isNewVideo = videoId == null;//No videoId means new video with the first chunk(Not the Oth Chunk)
        Optional<Video> video = Optional.empty();
        if(isNewVideo){
            String videoTitle = StringUtils.cleanPath(Objects.requireNonNull(videoFile.getOriginalFilename()));
            video = Optional.of(videoRepo.save(Video.builder().title(videoTitle).build()));
            videoId = video.get().getVideoId();
        }
        else{
            video = videoRepo.findById(videoId);
            if(video.isEmpty()){
                logger.error("VideoServiceImpl>saveVideoChunk():: No record Found with the given videoId({}) in the db.",videoId);
                throw new EntityNotFoundException("Wrong Video Id.");
            }
            else{
                if(chunkUploadStatus.isChunkAlreadyPresent(chunkIndex,videoId))
                    throw new ChunkAlreadyUploadedException(chunkIndex,videoId.toString());
            }
        }

        //getting the extension
        String videoTitle = video.get().getTitle();
        int indexOfLastDot = videoTitle.lastIndexOf('.');
        String fileExtension = indexOfLastDot>-1?videoTitle.substring(indexOfLastDot):"";


        //creating the temp directory
        Path videoDirPath = createSavedDirIfNotExists(Path.of(TEMP_VIDEO_DIR,videoId.toString()).toString());

        //making the name of the chunk
        AtomicInteger totalChunkCount = new AtomicInteger();
        chunkUploadStatus.getChunkStatus(videoId)
                .ifPresentOrElse(chunkData->totalChunkCount.set(chunkData.getTotalChunks()),()->totalChunkCount.set(totalChunks));
        String currentChunkName = chunkIndex + CHUNK_COUNT_SEPARATOR + totalChunkCount.get() + fileExtension;
        Path savingPath = videoDirPath.resolve(currentChunkName);

        //saving the chunk
        try (FileOutputStream fos = new FileOutputStream(savingPath.toFile(), true)) {
            fos.write(videoFile.getBytes());
            fos.flush();
        } catch (IOException e) {
            logger.debug("File({}) chuck{}of{}, saving failed",videoTitle,chunkIndex+1,totalChunkCount.get());
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        chunkUploadStatus.addUploadedChunk(videoId,chunkIndex,totalChunkCount.get()).ifPresent(chunkOpt->{
            if(chunkOpt.isAllChunksCollected())
            {
                logger.info("++++ VideoServiceImpl>saveVideoChunk():: Video({}) chunk saving completed. Start Merging...",videoTitle);
                mergeChunks(chunkOpt.getVideoId());
            }
        });
        return video;
    }

    private Path createSavedDirIfNotExists(String pathAsString){
        // Create a directory if it doesn't exist
        Path videoDirPath = Path.of(pathAsString);
        try {
            if (!Files.exists(videoDirPath)) {
                Files.createDirectories(videoDirPath);
                logger.info(String.format("++++ VideoServiceImpl>createSavedDirIfNotExists():: Creating the video directory: %s",videoDirPath.toAbsolutePath()));
            }
        } catch (IOException e) {
            logger.error("###{} - {}>createSavedDirIfNotExists",VideoServiceImpl.class.getName(),e.getMessage());
            throw new RuntimeException(String.format("Failed to create video directory: %s", videoDirPath), e);
        }
        return videoDirPath;
    }

    private void mergeChunks(UUID videoId){
        Optional<Video> video = videoRepo.findById(videoId);
        if(video.isEmpty()){
            logger.error("VideoServiceImpl>mergeChunks:: Merging chunks failed, video({}) is not found in db.",videoId);
            throw new EntityNotFoundException(String.format("No video record found with Id:%s.",videoId));
        }

        String title = video.get().getTitle();
        int indexOfLastDot = title.lastIndexOf('.');
        String extension = indexOfLastDot>-1?title.substring(indexOfLastDot):"";

        Path chunkedFilePath = Path.of(TEMP_VIDEO_DIR,videoId.toString());
        Path finalVideoPath = createSavedDirIfNotExists(SAVED_VIDEO_DIR).resolve(videoId+extension);

        try(FileOutputStream mergedStream = new FileOutputStream(finalVideoPath.toFile(),true)){
           Files.list(chunkedFilePath)
                   .filter(Files::isRegularFile)
                   .sorted(Comparator.comparingInt(file->{
                       String filename = file.getFileName().toString();
                       return Integer.parseInt(filename.split(CHUNK_COUNT_SEPARATOR)[0]);
                   }))
                   .forEachOrdered(chunkFile ->{
                       try{
                           mergedStream.write(Files.readAllBytes(chunkFile));
                           logger.info("++++ VideoServiceImpl>mergeChunks():: merge chunk({})",chunkFile.getFileName());
                       } catch (IOException e) {
                           logger.error("Failed merging chunk:{}\n{}.",chunkFile.getFileName(),e.getMessage());
                           throw new RuntimeException("Failed at merging chunks "+chunkFile.getFileName().toString().replace(CHUNK_COUNT_SEPARATOR,"/"));
                       }
                   });
        } catch (FileNotFoundException e) {
            logger.error("VideoServiceImpl>mergeChunks():: {}",e.getMessage());
            throw new RuntimeException();
        } catch (IOException e) {
            logger.error("VideoServiceImpl>mergeChunks():: Merging failed for video ID: {}\n{}", videoId,e.getMessage());
            throw new RuntimeException("Merging failed for video ID:"+videoId);
        }
        // Mark chunk status as merged
        chunkUploadStatus.markChunksMerged(videoId, true);

        // Clean up temp chunk files
        try {
            Files.walk(chunkedFilePath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            logger.info("++++ VideoServiceImpl>mergeChunks():: Temporary chunk directory deleted: {}", chunkedFilePath);
        } catch (IOException e) {
            logger.warn("---- VideoServiceImpl>mergeChunks():: Failed to delete temporary files for video: {}", videoId);
        }
    }
}