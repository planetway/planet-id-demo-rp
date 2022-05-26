package com.planetway.fudosan.service;

import com.planetway.fudosan.domain.RpFile;
import com.planetway.fudosan.repository.FileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FileServiceTest {

    private final FileRepository fileRepository = mock(FileRepository.class);
    private FileService service = new FileService(fileRepository);

    @Test
    public void storeFile() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("fileName.pdf");
        when(multipartFile.getContentType()).thenReturn("image/png");
        byte[] fileBytes = {1, 2};
        when(multipartFile.getBytes()).thenReturn(fileBytes);

        RpFile rpFile = service.storeFile(multipartFile);

        verify(fileRepository).save(rpFile);
        assertThat(rpFile.getFileId()).isNotEmpty();
        assertThat(rpFile.getFileName()).isEqualTo("fileName.pdf");
        assertThat(rpFile.getContentType()).isEqualTo("image/png");
        assertThat(rpFile.getData()).isEqualTo(fileBytes);
        assertThat(rpFile.getHash()).isEqualTo(DigestUtils.sha256Hex(fileBytes));
    }

    @Test
    public void readFile() {
        RpFile expectedFile = new RpFile();
        when(fileRepository.findById("uuid")).thenReturn(expectedFile);

        RpFile rpFile = service.readFile("uuid");
        assertThat(rpFile).isSameAs(expectedFile);
    }

}
