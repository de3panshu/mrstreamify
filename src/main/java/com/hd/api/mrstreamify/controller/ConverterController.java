package com.hd.api.mrstreamify.controller;

import com.hd.api.mrstreamify.dto.ApiResponseDto;
import com.hd.api.mrstreamify.entity.Video;
import com.hd.api.mrstreamify.service.ConvertService;
import com.hd.api.mrstreamify.service.VideoService;
import com.hd.api.mrstreamify.validation.annotation.ValidVideoFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/convert")
@SuppressWarnings("unused")
public class ConverterController {

    @Autowired
    VideoService videoSerivce;
    @PostMapping("/")
    //When multiple files are send in the "video" only the first one is received in the RequestParam
    public ResponseEntity<ApiResponseDto<Video>> SaveVideo(@RequestParam("video") @ValidVideoFormat MultipartFile video){
        Optional<Video> savedVideo =  videoSerivce.saveVideo(video);
        return savedVideo
                .map(videoObj -> ResponseEntity
                    .ok(ApiResponseDto.<Video>builder().data(videoObj).success(true).message("Video Saved Successfully.").build())
                )
                .orElseGet(()-> ResponseEntity
                        .internalServerError()
                        .body(ApiResponseDto.<Video>builder().success(false).message("Video Saving Failed.").build())
                );
    }
}
