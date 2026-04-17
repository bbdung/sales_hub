package com.apus.salehub.application.port.out.persistence;

import com.apus.salehub.domain.model.Project;
import com.apus.salehub.domain.model.enums.ProjectStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    Optional<Project> findByFingerprintAndIsDuplicateFalse(String fingerprint);

    long countByStatus(ProjectStatus status);

    @Query("SELECT AVG(p.score) FROM Project p")
    Double findAverageScore();

    @EntityGraph(attributePaths = {"source", "contact", "skills"})
    Optional<Project> findWithRelationsById(Long id);
}
