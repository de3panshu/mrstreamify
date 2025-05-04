package com.hd.api.mrstreamify.repo;

import com.hd.api.mrstreamify.entity.ChunkUploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChunkUploadStatusRepo extends JpaRepository<ChunkUploadStatus, UUID> {
}
