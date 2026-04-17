package com.apus.salehub.adapter.in.rest.dto.project;

import java.util.List;

public record ProjectListOutDto(List<ProjectOutDto> items, int total, int page, int pageSize) {}
