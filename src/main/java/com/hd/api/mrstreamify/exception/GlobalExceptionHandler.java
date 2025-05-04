package com.hd.api.mrstreamify.exception;

import com.hd.api.mrstreamify.dto.ApiResponseDto;
import com.hd.api.mrstreamify.entity.Video;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseDto<Video>> handleBadRequestExpcetion(BadRequestException ex){
        logger.error("BadRequestException: {} - Data: {}", ex.getMessage(), ex.getData());
        return ResponseEntity.badRequest().body(ApiResponseDto.<Video>builder().message(ex.getMessage()).data((Video)ex.getData()).build());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Video>> handleBadRequestExpcetion(Exception ex){
        logger.error("Exception: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponseDto.<Video>builder().message(ex.getMessage()).build());
    }
}
