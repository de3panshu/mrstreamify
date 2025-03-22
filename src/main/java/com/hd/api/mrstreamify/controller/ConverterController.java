package com.hd.api.mrstreamify.controller;

import com.hd.api.mrstreamify.entity.Video;
import com.hd.api.mrstreamify.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/convert")
public class ConverterController {

    @Autowired
    VideoService videoSerivce;
    @PostMapping("/")
    public ResponseEntity<?> SaveVideo(@RequestParam("video")MultipartFile video){
        Optional<Video> savedVideo =  videoSerivce.saveVideo(video);
        return savedVideo.isPresent()?
                ResponseEntity.ok(savedVideo.get()) :
                ResponseEntity
                        .internalServerError()
                        .body("Video saving failed");
    }
}
