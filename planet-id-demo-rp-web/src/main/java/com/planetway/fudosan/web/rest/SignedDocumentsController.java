package com.planetway.fudosan.web.rest;

import com.planetway.fudosan.domain.SignedDocumentEntity;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.repository.SignedDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/signed-documents")
public class SignedDocumentsController {
    private final SignedDocumentRepository signedDocumentRepository;

    @GetMapping
    public List<SignedDocumentEntity> getAll(@AuthenticationPrincipal UserInfo userInfo) {
        return signedDocumentRepository.getAllByUserId(userInfo.getId());
    }
}
