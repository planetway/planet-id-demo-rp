package com.planetway.fudosan.domain;

import lombok.Data;

@Data
public class RpFile {
    private String fileId;
    private String fileName;
    private String contentType;
    private String hash;
    private byte[] data;
}
