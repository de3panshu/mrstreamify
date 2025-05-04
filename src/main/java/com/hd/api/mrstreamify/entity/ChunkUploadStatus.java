package com.hd.api.mrstreamify.entity;

import com.hd.api.mrstreamify.service.ChunkUploadStatusService;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_chunk_upload_status")
public class ChunkUploadStatus {

    @Id
    private UUID videoId;

    @NotNull
    @Min(1)
    private int totalChunks;

    @ElementCollection
    @CollectionTable(name = "tbl_uploaded_chunks", joinColumns = @jakarta.persistence.JoinColumn(name = "video_id"))
    private Set<Integer> uploadedChunks;

    private boolean isChunksMerged;

    public void addNewChunk(int chunkIndex)throws IllegalArgumentException{
        if(totalChunks<=chunkIndex || chunkIndex<0)
            throw new IllegalArgumentException(String.format("Invalid value for chunkIndex:%d",chunkIndex));
        this.uploadedChunks.add(chunkIndex);
    }
    public boolean isAllChunksCollected(){
        return this.totalChunks == this.uploadedChunks.size();
    }
}

