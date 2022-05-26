package com.planetway.fudosan.xml.consent;


import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestData {
    private String planetId;
    private DataProvider dataProvider;
    private List<String> dataService;
    private DataConsumer dataConsumer;
}


