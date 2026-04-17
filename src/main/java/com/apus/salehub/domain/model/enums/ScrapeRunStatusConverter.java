package com.apus.salehub.domain.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ScrapeRunStatusConverter implements AttributeConverter<ScrapeRunStatus, String> {

    @Override
    public String convertToDatabaseColumn(ScrapeRunStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public ScrapeRunStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return ScrapeRunStatus.fromValue(dbData);
    }
}