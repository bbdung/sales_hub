package com.apus.salehub.domain.model;

import com.apus.salehub.domain.model.enums.ScrapeRunStatus;
import com.apus.salehub.domain.model.enums.ScrapeRunStatusConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "scrape_runs")
public class ScrapeRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false, insertable = false, updatable = false)
    private Long sourceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Convert(converter = ScrapeRunStatusConverter.class)
    @Column(length = 50, nullable = false)
    private ScrapeRunStatus status = ScrapeRunStatus.RUNNING;

    @Column(name = "projects_found", nullable = false)
    private int projectsFound = 0;

    @Column(name = "projects_new", nullable = false)
    private int projectsNew = 0;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_used")
    private Map<String, Object> configUsed;

    @PrePersist
    protected void onCreate() {
        if (startedAt == null) startedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }

    public Source getSource() { return source; }
    public void setSource(Source source) {
        this.source = source;
        this.sourceId = source != null ? source.getId() : null;
    }

    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }

    public OffsetDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(OffsetDateTime finishedAt) { this.finishedAt = finishedAt; }

    public ScrapeRunStatus getStatus() { return status; }
    public void setStatus(ScrapeRunStatus status) { this.status = status; }

    public int getProjectsFound() { return projectsFound; }
    public void setProjectsFound(int projectsFound) { this.projectsFound = projectsFound; }

    public int getProjectsNew() { return projectsNew; }
    public void setProjectsNew(int projectsNew) { this.projectsNew = projectsNew; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Map<String, Object> getConfigUsed() { return configUsed; }
    public void setConfigUsed(Map<String, Object> configUsed) { this.configUsed = configUsed; }
}
