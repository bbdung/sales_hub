package com.apus.salehub.adapter.in.rest.dto.source;

import java.time.OffsetDateTime;
import java.util.Map;

public record SourceOutDto(
    Long id,
    String name,
    String displayName,
    String baseUrl,
    boolean isActive,
    Map<String, Object> configJson,
    OffsetDateTime createdAt
) {}
