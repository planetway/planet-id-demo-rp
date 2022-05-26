package com.planetway.fudosan.xml.consent;

import com.planetway.fudosan.xml.adapter.DateAdapter;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@XmlRootElement(name = "signatureInput")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConsentDocument {
    private String signRequestType;
    private UUID requestUUID;
    private String requestURI;
    @XmlJavaTypeAdapter(DateAdapter.class)
    private OffsetDateTime validTill;
    private Boolean revokable;
    private RequestData data;
}

