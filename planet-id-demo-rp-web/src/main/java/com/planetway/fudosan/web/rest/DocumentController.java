package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.service.DocumentContainerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("document")
@RestController
public class DocumentController {
    private final DocumentContainerService documentContainerService;

    public DocumentController(DocumentContainerService documentContainerService) {
        this.documentContainerService = documentContainerService;
    }

    @PostMapping(value = "sign-container", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> createFileSigningContainer(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(documentContainerService.createFileSignContainer(file));
    }
}
