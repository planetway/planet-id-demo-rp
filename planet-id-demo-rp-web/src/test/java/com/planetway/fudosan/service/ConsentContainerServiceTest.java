package com.planetway.fudosan.service;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.configuration.RaProperties;
import com.planetway.fudosan.domain.ConsentContainerRequest;
import com.planetway.rp.configuration.PCoreProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;


public class ConsentContainerServiceTest {

    private static final String CONTAINER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<signatureInput xmlns=\"https://www.planetway.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<signRequestType>consent_give</signRequestType>" +
            "<requestUUID>25f71a40-ada0-4548-ac33-739056f17933</requestUUID>" +
            "<requestURI>https://api.planetway.com/signrequests/?uuid=25f71a40-ada0-4548-ac33-739056f17933</requestURI>" +
            "<validTill>2021-12-16T20:00:00Z</validTill>" +
            "<revokable>true</revokable>" +
            "<data>" +
            "<planetId>100000000000</planetId>" +
            "<dataProvider>" +
            "<relyingPartyCode>10</relyingPartyCode>" +
            "<planetXCode>JP-TEST.COM.12973914</planetXCode>" +
            "</dataProvider>" +
            "<dataService>JP-TEST.COM.12973914.addresses.addresses.v1</dataService>" +
            "<dataConsumer>" +
            "<relyingPartyCode>22</relyingPartyCode>" +
            "<planetXCode>JP-TEST.COM.11111111</planetXCode>" +
            "</dataConsumer>" +
            "</data>" +
            "</signatureInput>";

    private ConsentContainerService service;

    @BeforeEach
    public void setUp() throws JAXBException {
        AppProperties appProperties = new AppProperties();
        RaProperties raProperties = new RaProperties();
        PCoreProperties pcoreProperties = new PCoreProperties();
        service = spy(new ConsentContainerService(appProperties, raProperties, pcoreProperties));
    }

    @Test
    public void createConsentContainer() {
        ConsentContainerRequest request = new ConsentContainerRequest();
        request.setRequestURI("https://api.planetway.com/signrequests/?uuid=25f71a40-ada0-4548-ac33-739056f17933");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2021-12-17T00:00:00.00+04:00");
        request.setValidTill(offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC));
        request.setRevokable(true);
        request.setPlanetId("100000000000");
        request.setDataService("JP-TEST.COM.12973914.addresses.addresses.v1");

        request.setDataProviderRelyingPartyCode("10");
        request.setDataProviderPlanetXCode("JP-TEST.COM.12973914");

        request.setDataConsumerRelyingPartyCode("22");
        request.setDataConsumerPlanetXCode("JP-TEST.COM.11111111");

        doReturn(UUID.fromString("25f71a40-ada0-4548-ac33-739056f17933")).when(service).generateUUID();

        String response = service.createConsentDocument(request);

        assertThat(response).isEqualTo(CONTAINER_XML);
    }
}
