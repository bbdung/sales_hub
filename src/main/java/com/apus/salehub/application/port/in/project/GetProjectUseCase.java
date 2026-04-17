package com.apus.salehub.application.port.in.project;

import com.apus.salehub.domain.model.Project;

public interface GetProjectUseCase {

    Project getProject(Long id);
}