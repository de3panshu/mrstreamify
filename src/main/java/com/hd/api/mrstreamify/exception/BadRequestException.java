package com.hd.api.mrstreamify.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadRequestException extends ApiStreamifyException{
    public BadRequestException(Object data,String message){
        super(data,message,false);
    }
}
