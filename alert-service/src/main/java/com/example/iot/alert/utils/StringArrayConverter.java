package com.example.iot.alert.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringArrayConverter implements AttributeConverter<String[], String> {

    private static final String SEP = ",";

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        return attribute == null
                ? null
                : String.join(SEP, attribute);
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        return dbData == null
                ? new String[0]
                : dbData.split(SEP);
    }
}
