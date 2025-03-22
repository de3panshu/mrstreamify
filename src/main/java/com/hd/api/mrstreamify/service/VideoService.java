package com.hd.api.mrstreamify.service;

import com.hd.api.mrstreamify.entity.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface VideoService {
     Optional<Video> saveVideo(MultipartFile video);
}
