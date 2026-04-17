package com.apus.salehub.domain.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BudgetTypeConverter implements AttributeConverter<BudgetType, String> {

    @Override
    public String convertToDatabaseColumn(BudgetType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public BudgetType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return BudgetType.fromValue(dbData);
    }
}