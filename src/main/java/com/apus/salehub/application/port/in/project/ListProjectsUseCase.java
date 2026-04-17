package com.apus.salehub.application.port.in.project;

import com.apus.salehub.domain.model.Project;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface ListProjectsUseCase {

    Page<Project> listProjects(int page, int pageSize, String status,
                                Long sourceId, BigDecimal minScore,
                                String search, String sort);
}