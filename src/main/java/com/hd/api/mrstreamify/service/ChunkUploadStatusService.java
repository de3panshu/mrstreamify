package com.hd.api.mrstreamify.service;

import com.hd.api.mrstreamify.entity.ChunkUploadStatus;

import java.util.Optional;
import java.util.UUID;

public interface ChunkUploadStatusService {

    /**
     * Updates the uploadedChunks list by adding a new chunk index for the given video ID.
     *
     * @param videoId UUID of the video.
     * @param savedChunkIndex Index of the newly uploaded chunk.
     * @return Updated ChunkUploadStatus if found.
     */
    Optional<ChunkUploadStatus> addUploadedChunk(UUID videoId, int savedChunkIndex);

    /**
     * Updates the uploadedChunks list by adding a new chunk index for the given video ID.
     *
     * @param videoId UUID of the video.
     * @param savedChunkIndex Index of the newly uploaded chunk.
     * @param totalChunks Count of the total chunks will be uploaded.
     * @return Updated ChunkUploadStatus if found.
     */
    Optional<ChunkUploadStatus> addUploadedChunk(UUID videoId, int savedChunkIndex,int totalChunks);
    /**
     * Updates the status to mark that all chunks have been merged successfully.
     *
     * @param videoId UUID of the video.
     * @param mergeCompleted true if chunks are merged successfully.
     * @return Updated ChunkUploadStatus if found.
     */
    Optional<ChunkUploadStatus> markChunksMerged(UUID videoId, boolean mergeCompleted);

    /**
     * Retrieves the current chunk upload status by video ID.
     *
     * @param videoId UUID of the video.
     * @return Optional ChunkUploadStatus.
     */
    Optional<ChunkUploadStatus> getChunkStatus(UUID videoId);

    boolean isChunkAlreadyPresent(int chunkIndex, UUID videoId);
}

