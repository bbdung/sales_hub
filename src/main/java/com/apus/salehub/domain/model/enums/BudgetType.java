package com.apus.salehub.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BudgetType {
    FIXED("fixed"),
    HOURLY("hourly"),
    MONTHLY("monthly");

    private final String value;

    BudgetType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static BudgetType fromValue(String value) {
        if (value == null) return null;
        for (BudgetType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown budget type: " + value);
    }
}
