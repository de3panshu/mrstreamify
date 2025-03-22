package com.hd.api.mrstreamify.service.impl;

import com.hd.api.mrstreamify.entity.Video;
import com.hd.api.mrstreamify.repo.VideoRepo;
import com.hd.api.mrstreamify.service.VideoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

@Service
public class VideoServiceImpl implements VideoService {
    private static final Logger logger = LogManager.getLogger(VideoService.class);

    @Value("${app.dir}")
    private String VIDEO_DIR;

    @Autowired
    private VideoRepo videoRepo;

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

        // Create a directory if it doesn't exist
        Path videoDirPath = Path.of(VIDEO_DIR);
        try {
            if (!Files.exists(videoDirPath)) {
                Files.createDirectories(videoDirPath);
                logger.info(String.format("Creating the video directory: %s",videoDirPath.toAbsolutePath()));
            }
        } catch (IOException e) {
            logger.error("Failed to create video directory:");
            throw new RuntimeException(String.format("Failed to create video directory: %s", VIDEO_DIR), e);
        }
        //saving the video
        String newFileName = video.getVideoId().toString() + fileExtension;
        Path savingPath = videoDirPath.resolve(newFileName);

        try {
            Files.copy(videoFile.getInputStream(), savingPath);
        } catch (IOException e) {
            videoRepo.delete(video); //deleting the saved the video record.
            logger.error(String.format("File(%s) saving failed, deleting the db record",video.getTitle()));
            throw new RuntimeException(e);
        }
        return Optional.of(video);
    }
}