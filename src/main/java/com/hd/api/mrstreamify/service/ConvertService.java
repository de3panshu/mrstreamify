package com.hd.api.mrstreamify.service;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ConvertService {

    private static final Logger logger = LogManager.getLogger(ConvertService.class);

    @Getter
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mpeg", "video/quicktime", "video/x-msvideo", "video/webm"
    );

    public static boolean isAllowedType(MultipartFile videoFile){
        if(videoFile.isEmpty()){
            logger.debug("ConvertService>isAllowedType: Empty video file.");
            return false;
        }
        boolean result = false;
        try{
            String mimeType = new Tika().detect(videoFile.getInputStream());
            result = ALLOWED_VIDEO_TYPES.contains(mimeType);
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
        return result;
    }
}
