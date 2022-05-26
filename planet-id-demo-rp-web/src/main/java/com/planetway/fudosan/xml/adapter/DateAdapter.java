package com.planetway.fudosan.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateAdapter extends XmlAdapter<String, OffsetDateTime> {

    @Override
    public OffsetDateTime unmarshal(String date) {
        return null;
    }

    @Override
    public String marshal(OffsetDateTime date) {
        return date.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
