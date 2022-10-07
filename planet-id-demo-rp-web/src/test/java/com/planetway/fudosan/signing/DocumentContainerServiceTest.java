package com.planetway.fudosan.signing;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.RpFile;
import com.planetway.fudosan.service.DocumentContainerService;
import com.planetway.fudosan.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocumentContainerServiceTest {
    private static final String CONTAINER_XML = // "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<signatureInput xmlns=\"https://www.planetway.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "    <signRequestType>file_sign</signRequestType>\n" +
            "    <hashToSign>E6117224D19FCAD4A5C907344A8AA0B8F995D1C9FE99989BA67B628B41525130</hashToSign>\n" +
            "    <fileURI>http://localhost/files/fileId</fileURI>\n" +
            "    <data>\n" +
            "        <fileName>file_to_be_signed.pdf</fileName>\n" +
            "    </data>\n" +
            "</signatureInput>";
    private final FileService fileRepository = mock(FileService.class);
    private final DocumentContainerService service;

    public DocumentContainerServiceTest() throws JAXBException {
        AppProperties appProperties = new AppProperties();
        appProperties.setBaseUrl("http://localhost");
        service = new DocumentContainerService(fileRepository, appProperties);
    }

    @Test
    public void createFileSignContainer() {
        MultipartFile multipartFile = mock(MultipartFile.class);
        RpFile rpFile = new RpFile();
        rpFile.setFileId("fileId");
        rpFile.setFileName("file_to_be_signed.pdf");
        rpFile.setHash("E6117224D19FCAD4A5C907344A8AA0B8F995D1C9FE99989BA67B628B41525130");
        when(fileRepository.storeFile(multipartFile)).thenReturn(rpFile);

        String response = service.createFileSignContainer(multipartFile);

        assertThat(response).isEqualTo(CONTAINER_XML);
    }
}
