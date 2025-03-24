package com.hd.api.mrstreamify.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {
    private T data;
    private boolean success;
    private String message;
}
