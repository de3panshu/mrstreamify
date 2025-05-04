package com.hd.api.mrstreamify.service;

import com.hd.api.mrstreamify.entity.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface VideoService {
     Optional<Video> saveVideo(MultipartFile video);

     /**
      * Handles chunked upload of video files.
      * <p>
      * This method saves each chunk individually to a temporary folder named after the videoId.
      * When all chunks are received (based on the totalChunks count), an asynchronous operation
      * is triggered to merge them into a complete video file.
      * </p>
      *
      * <p><strong>Process Overview:</strong></p>
      * <ul>
      *   <li>The first chunk (not necessarily chunk 0) is uploaded with a <code>null</code> videoId to initiate the session.</li>
      *   <li>A new video record is created in the database, and a folder with the assigned UUID is created to store all chunks.</li>
      *   <li>Each chunk is stored with a filename pattern: <code>&lt;chunkIndex&gt;__&lt;totalChunks&gt;</code>.</li>
      *   <li>After each chunk is saved, the service checks asynchronously whether all chunks have been received.</li>
      *   <li>Once complete, the chunks are merged into a single video file, the folder is deleted, and the final file is saved.</li>
      * </ul>
      *
      * @param chunk        The video file chunk being uploaded.
      * @param videoId      The UUID identifying the video upload session. This is null for the first initiating chunk.
      * @param chunkIndex   The index (zero-based) of the current chunk in the total set.
      * @param totalChunks  The total number of chunks expected for the video upload.
      * @return An {@link Optional} containing the saved {@link Video} object if all chunks are uploaded and merged,
      *         or an empty Optional if merging has not yet occurred.
      * @throws RuntimeException if saving the chunk fails or directories cannot be created.
      * @throws IllegalArgumentException if parameters are invalid (e.g., negative chunk index or null file).
      */
     Optional<Video> saveVideoChunk(MultipartFile chunk, UUID videoId, int chunkIndex, int totalChunks);
}
