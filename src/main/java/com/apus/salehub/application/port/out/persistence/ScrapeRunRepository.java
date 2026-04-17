package com.apus.salehub.application.port.out.persistence;

import com.apus.salehub.domain.model.ScrapeRun;
import com.apus.salehub.domain.model.enums.ScrapeRunStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapeRunRepository extends JpaRepository<ScrapeRun, Long> {

    List<ScrapeRun> findByStatus(ScrapeRunStatus status);

    @EntityGraph(attributePaths = {"source"})
    List<ScrapeRun> findAllByOrderByStartedAtDesc(Pageable pageable);
}
