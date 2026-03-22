package com.lulo.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
}
