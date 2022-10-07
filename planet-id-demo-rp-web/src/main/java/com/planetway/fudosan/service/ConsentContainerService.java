package com.planetway.fudosan.service;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.configuration.RaProperties;
import com.planetway.fudosan.domain.ConsentContainerRequest;
import com.planetway.fudosan.domain.ConsentRevokeDocument;
import com.planetway.fudosan.domain.DataBank;
import com.planetway.fudosan.xml.consent.ConsentDocument;
import com.planetway.fudosan.xml.consent.DataConsumer;
import com.planetway.fudosan.xml.consent.DataProvider;
import com.planetway.fudosan.xml.consent.RequestData;
import com.planetway.rp.configuration.PCoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
public class ConsentContainerService {
    private final JAXBContext jaxbContext = createJaxbContext(ConsentDocument.class, ConsentRevokeDocument.class);

    private final Marshaller marshaller = jaxbContext.createMarshaller();
    private final AppProperties appProperties;
    private final RaProperties raProperties;
    private final PCoreProperties pcoreProperties;

    public ConsentContainerService(AppProperties appProperties, RaProperties raProperties, PCoreProperties pcoreProperties) throws JAXBException {
        this.appProperties = appProperties;
        this.raProperties = raProperties;
        this.pcoreProperties = pcoreProperties;
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        // marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    }

    private JAXBContext createJaxbContext(Class... contextClasses) {
        try {
            return JAXBContext.newInstance(contextClasses);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated(forRemoval = true)
    public String createConsentDocument(ConsentContainerRequest request) {
        ConsentDocument consentDocument = new ConsentDocument();
        consentDocument.setSignRequestType("consent_give");
        consentDocument.setRequestUUID(generateUUID());
        consentDocument.setRequestURI(request.getRequestURI());
        consentDocument.setValidTill(request.getValidTill());
        consentDocument.setRevokable(request.getRevokable());
        consentDocument.setData(createRequestData(request));
        return marshalDocumentToString(consentDocument);
    }

    public String createConsentRevokeDocument(String consentUUID, String planetId) {
        ConsentRevokeDocument doc = new ConsentRevokeDocument();
        doc.setRequestUUID(generateUUID().toString());
        doc.setConsentUUID(consentUUID);
        doc.setTargetUserId(planetId);

        StringWriter stringWriter = new StringWriter();
        try {
            marshaller.marshal(doc, stringWriter);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        return stringWriter.toString();
    }

    public String createConsentContainerForRa(String planetId, UUID consentId) {
        ConsentDocument consentDocument = new ConsentDocument();
        consentDocument.setSignRequestType("consent_give");

        consentDocument.setRequestUUID(consentId);
        consentDocument.setRequestURI(pcoreProperties.getUrl() + "?uuid=" + consentId.toString());
        consentDocument.setValidTill(OffsetDateTime.now().plus(1, ChronoUnit.YEARS));
        consentDocument.setRevokable(true);

        RequestData consentData = new RequestData();
        consentData.setPlanetId(planetId);
        consentData.setDataService(new ArrayList<>());
        String dataService = raProperties.getPlanetxSubsystem().toString()
                + "/" + raProperties.getPersonInfoServiceCode();
        consentData.getDataService().add(dataService);

        DataProvider dataProvider = new DataProvider();
        dataProvider.setRelyingPartyCode(raProperties.getPcoreRpId());
        dataProvider.setPlanetXCode(raProperties.getPlanetxSubsystem().toString());
        consentData.setDataProvider(dataProvider);

        DataConsumer dataConsumer = new DataConsumer();
        dataConsumer.setRelyingPartyCode(pcoreProperties.getRpId());
        dataConsumer.setPlanetXCode(appProperties.getPlanetXSubsystem().toString());
        consentData.setDataConsumer(dataConsumer);

        consentDocument.setData(consentData);
        String documentXml = marshalDocumentToString(consentDocument);

        log.debug("Creating consent document for planetId {} {}", planetId, documentXml);

        return documentXml;
    }

    public String createConsentDocumentForProvider(String dataBankName, String forPlanetId) {
        DataBank dataBank = appProperties.getDataBanks().get(dataBankName);

        String fullProviderPxService = dataBank.getPlanetXService().toString();

        ConsentContainerRequest consentContainerRequest = new ConsentContainerRequest();

        consentContainerRequest.setValidTill(OffsetDateTime.now().plus(1, ChronoUnit.YEARS));
        consentContainerRequest.setRevokable(true);

        consentContainerRequest.setPlanetId(forPlanetId);
        consentContainerRequest.setDataService(fullProviderPxService);

        consentContainerRequest.setDataProviderRelyingPartyCode(dataBank.getRelyingPartyCode());
        consentContainerRequest.setDataProviderPlanetXCode(dataBank.getPlanetXService().toSubsystemIdentifierString());
        consentContainerRequest.setDataConsumerRelyingPartyCode(pcoreProperties.getRpId());
        consentContainerRequest.setDataConsumerPlanetXCode(appProperties.getPlanetXSubsystem().toString());

        log.debug("Creating consent document for planetId {} {}", forPlanetId, consentContainerRequest);

        return createConsentDocument(consentContainerRequest);
    }

    private RequestData createRequestData(ConsentContainerRequest request) {
        RequestData requestData = new RequestData();
        requestData.setPlanetId(request.getPlanetId());
        requestData.setDataService(new ArrayList<>());
        requestData.getDataService().add(request.getDataService());

        DataProvider dataProvider = new DataProvider();
        dataProvider.setRelyingPartyCode(request.getDataProviderRelyingPartyCode());
        dataProvider.setPlanetXCode(request.getDataProviderPlanetXCode());
        requestData.setDataProvider(dataProvider);

        DataConsumer dataConsumer = new DataConsumer();
        dataConsumer.setRelyingPartyCode(request.getDataConsumerRelyingPartyCode());
        dataConsumer.setPlanetXCode(request.getDataConsumerPlanetXCode());
        requestData.setDataConsumer(dataConsumer);

        return requestData;
    }

    private String marshalDocumentToString(ConsentDocument document) {
        StringWriter stringWriter = new StringWriter();
        try {
            marshaller.marshal(document, stringWriter);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        return stringWriter.toString();
    }

    public UUID generateUUID() {
        return UUID.randomUUID();
    }
}

