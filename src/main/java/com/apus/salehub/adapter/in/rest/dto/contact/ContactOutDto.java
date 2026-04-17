package com.apus.salehub.adapter.in.rest.dto.contact;

import java.time.OffsetDateTime;

public record ContactOutDto(
    Long id,
    String name,
    String email,
    String phone,
    String company,
    String linkedinUrl,
    String profileUrl,
    String location,
    String notes,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
