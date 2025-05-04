package com.hd.api.mrstreamify.repo;

import com.hd.api.mrstreamify.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoRepo extends JpaRepository<Video, UUID> {
}
