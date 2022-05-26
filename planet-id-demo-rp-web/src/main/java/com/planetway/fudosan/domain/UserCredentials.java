package com.planetway.fudosan.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserCredentials {

    @NotBlank
    @Pattern(regexp = ".+@.+")
    private String user;

    @NotBlank
    private String password;
}
