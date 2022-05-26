package com.planetway.fudosan.xml.consent;


import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "dataProvider")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataProvider {
    private String relyingPartyCode;
    private String planetXCode;
}
