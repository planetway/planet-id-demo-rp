package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.domain.RpFile;
import com.planetway.fudosan.service.FileService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example REST service for downloading stored files
 */
@RequestMapping("files")
@RestController
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = "/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        RpFile rpFile = fileService.readFile(fileId);
        Resource fileResource = new ByteArrayResource(rpFile.getData());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(rpFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + rpFile.getFileName() + "\"")
                .body(fileResource);
    }
}
