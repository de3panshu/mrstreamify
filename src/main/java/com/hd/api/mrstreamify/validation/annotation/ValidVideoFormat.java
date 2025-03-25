package com.hd.api.mrstreamify.validation.annotation;

import com.hd.api.mrstreamify.validation.VideoFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = VideoFormatValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVideoFormat {
    String message() default "Unsupported video file format.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
