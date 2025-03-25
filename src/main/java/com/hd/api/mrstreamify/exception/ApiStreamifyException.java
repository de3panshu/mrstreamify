package com.hd.api.mrstreamify.exception;

import jakarta.persistence.GeneratedValue;
import lombok.*;

@Getter
@Setter
public abstract class ApiStreamifyException extends RuntimeException {
    protected Object data;
    protected boolean success;

    protected ApiStreamifyException(Object data, String message, boolean success){
        super(message);
        this.data = data;
        this.success = success;
    }
}
