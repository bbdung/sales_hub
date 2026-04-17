package com.apus.salehub.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ScrapeRunStatus {
    RUNNING("running"),
    SUCCESS("success"),
    FAILED("failed");

    private final String value;

    ScrapeRunStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ScrapeRunStatus fromValue(String value) {
        for (ScrapeRunStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown scrape run status: " + value);
    }
}
