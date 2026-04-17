package com.apus.salehub.application.port.in.project;

import com.apus.salehub.domain.model.Project;
import com.apus.salehub.domain.model.enums.ProjectStatus;

import java.math.BigDecimal;
import java.util.Map;

public interface UpdateProjectUseCase {

    Project updateProject(Long id, ProjectStatus status, String notes, Long contactId);

    void deleteProject(Long id);

    record RescoreResult(BigDecimal score, Map<String, Object> breakdown) {}

    RescoreResult rescoreProject(Long id);
}