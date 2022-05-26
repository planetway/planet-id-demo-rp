package com.planetway.fudosan.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {

    private String errorCode;

    private String message;
}
