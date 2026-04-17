package com.apus.salehub.application.port.in.project;

import com.apus.salehub.domain.model.Project;

public interface CreateProjectUseCase {

    Project createProject(Project project, Long sourceId, Long contactId);
}