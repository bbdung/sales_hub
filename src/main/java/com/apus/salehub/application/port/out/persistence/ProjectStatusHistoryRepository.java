package com.apus.salehub.application.port.out.persistence;

import com.apus.salehub.domain.model.ProjectStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectStatusHistoryRepository extends JpaRepository<ProjectStatusHistory, Long> {
}
