package com.planetway.fudosan.planetx;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.PlanetXService;
import lombok.extern.slf4j.Slf4j;
import org.niis.xrd4j.client.SOAPClient;
import org.niis.xrd4j.client.SOAPClientImpl;
import org.niis.xrd4j.client.deserializer.ServiceResponseDeserializer;
import org.niis.xrd4j.client.serializer.ServiceRequestSerializer;
import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.springframework.stereotype.Service;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class PlanetXSoapTemplate {

    private final AppProperties appProperties;

    public PlanetXSoapTemplate(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * @param planetId the identifier of the end user whose consent for data access must be present
     * @return hashmap of attributes in the PlanetX response or
     * null in case of ANY Fault PlanetX response (both the Unauthorized case where there is no consent and various error situations like wrong provider/service/etc)
     */
    public Map<String, String> execute(String planetId, PlanetXService planetXService) {
        try {
            ProducerMember producer = producerMember(planetXService);
            String requestId = UUID.randomUUID().toString();
            ServiceRequest<String> request = new ServiceRequest<>(consumerMember(), producer, requestId);
            ServiceRequestSerializer serializer = new PlanetXRequestSerializer();
            ServiceResponseDeserializer deserializer = new PlanetXMapResponseDeserializer("row");
            request.setRequestData(planetId);
            serializer.serialize(request);
            SOAPClient client = new SOAPClientImpl();
            SOAPMessage soapRequest = request.getSoapMessage();
            SOAPMessage soapResponse = client.send(soapRequest, appProperties.getPlanetXSecurityServerUrl());
            if (soapResponse.getSOAPBody().hasFault()) {
                log.info("SOAP response returned failure: " + soapResponse.getSOAPBody().getFault().getFaultCode() +
                        "; " + soapResponse.getSOAPBody().getFault().getFaultString() +
                        "; pid=" + planetId + ", producer=" + producer
                );
                if ("Internal Server Error".equalsIgnoreCase(soapResponse.getSOAPBody().getFault().getFaultString())) {
                    throw new RuntimeException("Data server returned catastrophic error: " + soapResponse.getSOAPBody().getFault().getFaultString());
                }
                return null;
            } else {
                ServiceResponse<String, Map<String, String>> serviceResponse = deserializer.deserialize(soapResponse);
                return serviceResponse.getResponseData();
            }

        } catch (XRd4JException | SOAPException e) {
            throw new RuntimeException(e);
        }
    }

    public String execute(PlanetXService planetXService) {
        try {
            ProducerMember producer = producerMember(planetXService);
            String requestId = UUID.randomUUID().toString();
            ServiceRequest<String> request = new ServiceRequest<>(consumerMember(), producer, requestId);
            ServiceRequestSerializer serializer = new PlanetXRequestSerializer();
            ServiceResponseDeserializer deserializer = new PlanetXMapResponseDeserializer("row");
            serializer.serialize(request);
            SOAPClient client = new SOAPClientImpl();
            SOAPMessage soapRequest = request.getSoapMessage();
            SOAPMessage soapResponse = client.send(soapRequest, appProperties.getPlanetXSecurityServerUrl());
            if (soapResponse.getSOAPBody().hasFault()) {
                throw new RuntimeException(soapResponse.getSOAPBody().getFault().getFaultString());
            } else {
                ServiceResponse<String, Map<String, String>> serviceResponse = deserializer.deserialize(soapResponse);
                return serviceResponse.getResponseData().toString();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ConsumerMember consumerMember() {
        try {
            return new ConsumerMember(
                    appProperties.getPlanetXSubsystem().getInstance(),
                    appProperties.getPlanetXSubsystem().getMemberClass(),
                    appProperties.getPlanetXSubsystem().getMemberCode(),
                    appProperties.getPlanetXSubsystem().getSubsystemCode()
            );
        } catch (XRd4JException e) {
            throw new RuntimeException("Failed to create consumer member from "
                    + appProperties.getPlanetXSubsystem());
        }
    }

    private ProducerMember producerMember(PlanetXService planetXService) {
        try {
            ProducerMember producer = new ProducerMember(
                    planetXService.getInstance(),
                    planetXService.getMemberClass(),
                    planetXService.getMemberCode(),
                    planetXService.getSubsystemCode(),
                    planetXService.getServiceCode()
            );
            producer.setServiceVersion("v1"); // default

            producer.setNamespacePrefix("ns5");
            producer.setNamespaceUrl("http://producer.x-road.eu");

            return producer;
        } catch (XRd4JException e) {
            throw new RuntimeException("Failed to create consumer member from "
                    + appProperties.getPlanetXSubsystem());
        }
    }
}
