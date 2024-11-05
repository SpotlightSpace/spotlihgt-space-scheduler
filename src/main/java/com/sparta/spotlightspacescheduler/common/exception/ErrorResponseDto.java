package com.sparta.spotlightspacescheduler.common.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class ErrorResponseDto {

    private final int statusCode;
    private final String message;
    private final Map<String, String> validations = new HashMap<>();

    private ErrorResponseDto(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static ErrorResponseDto of(int statusCode, String message) {
        return new ErrorResponseDto(statusCode, message);
    }

    public void addValidation(String fieldName, String message) {
        validations.put(fieldName, message);
    }
}
