package com.apus.salehub.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectStatus {
    NEW("new"),
    IN_PIPELINE("in_pipeline"),
    REVIEWING("reviewing"),
    CONTACTED("contacted"),
    APPLIED("applied"),
    INTERVIEWING("interviewing"),
    NEGOTIATING("negotiating"),
    WON("won"),
    LOST("lost"),
    EXPIRED("expired"),
    ARCHIVED("archived");

    private final String value;

    ProjectStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ProjectStatus fromValue(String value) {
        for (ProjectStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown project status: " + value);
    }
}
