package com.hd.api.mrstreamify.validation;

import com.hd.api.mrstreamify.exception.BadRequestException;
import com.hd.api.mrstreamify.service.ConvertService;
import com.hd.api.mrstreamify.validation.annotation.ValidVideoFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class VideoFormatValidator implements ConstraintValidator<ValidVideoFormat, MultipartFile> {
    private static final Logger logger = LogManager.getLogger(VideoFormatValidator.class);
    private String errorMessage;
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mpeg", "video/quicktime", "video/x-msvideo", "video/webm","application/octet-stream"
    );
    @Override
    public void initialize(ValidVideoFormat constraintAnnotation) {
        this.errorMessage = constraintAnnotation.message();
    }
    @Override
    public boolean isValid(MultipartFile videoFile, ConstraintValidatorContext constraintValidatorContext) {
        if(videoFile.isEmpty()){
            logger.debug("ConvertService>isAllowedType: Empty video file.");
            throw new BadRequestException(null,"Empty video file.");
        }
        try{
            String mimeType = new Tika().detect(videoFile.getInputStream());
            if(!ALLOWED_VIDEO_TYPES.contains(mimeType))
                throw new BadRequestException(null, this.errorMessage);
        }
        catch (IOException ex){
            logger.debug(ex.getMessage());
            throw new BadRequestException(null, "Error processing video file.");
        }
        return true;
    }
}
