package com.hd.api.mrstreamify.service.impl;

import com.hd.api.mrstreamify.entity.ChunkUploadStatus;
import com.hd.api.mrstreamify.repo.ChunkUploadStatusRepo;
import com.hd.api.mrstreamify.service.ChunkUploadStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChunkUploadStatusServiceImpl implements ChunkUploadStatusService {
    @Autowired
    ChunkUploadStatusRepo chunkStatusRepo;
    @Override
    public Optional<ChunkUploadStatus> addUploadedChunk(UUID videoId, int toBeSavedChunkIndex, int totalChunksCount) {
        Optional<ChunkUploadStatus> chunkStatus = chunkStatusRepo.findById(videoId);
        if(chunkStatus.isEmpty()){// means it is the first chunk which is now uploaded.
            chunkStatus = Optional.of(ChunkUploadStatus.builder().videoId(videoId).totalChunks(totalChunksCount).uploadedChunks(new HashSet<>()).build());
        }
        chunkStatus.get().addNewChunk(toBeSavedChunkIndex);
        chunkStatusRepo.save(chunkStatus.get());
        return chunkStatus;
    }
    @Override
    public Optional<ChunkUploadStatus> addUploadedChunk(UUID videoId, int toBeSavedChunkIndex) {
        return this.addUploadedChunk(videoId,toBeSavedChunkIndex,-1);
    }
    @Override
    public Optional<ChunkUploadStatus> markChunksMerged(UUID videoId, boolean mergeCompleted) {
        return chunkStatusRepo.findById(videoId).map(chunkData->{
            chunkData.setChunksMerged(mergeCompleted);
            return chunkStatusRepo.save(chunkData);
        });
    }

    @Override
    public Optional<ChunkUploadStatus> getChunkStatus(UUID videoId) {
        return chunkStatusRepo.findById(videoId);
    }
}
