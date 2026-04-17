package com.apus.salehub.adapter.in.rest.controller;

import com.apus.base.response.ResponseWrapper;
import com.apus.salehub.adapter.in.rest.dto.project.*;
import com.apus.salehub.application.port.in.project.CreateProjectUseCase;
import com.apus.salehub.application.port.in.project.GetProjectUseCase;
import com.apus.salehub.application.port.in.project.ListProjectsUseCase;
import com.apus.salehub.application.port.in.project.UpdateProjectUseCase;
import com.apus.salehub.domain.model.Project;
import com.apus.salehub.domain.model.enums.BudgetType;
import com.apus.salehub.domain.model.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project", description = "Project management operations")
@ResponseWrapper
public class ProjectController {

    private final ListProjectsUseCase listProjectsUseCase;
    private final GetProjectUseCase getProjectUseCase;
    private final CreateProjectUseCase createProjectUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;

    public ProjectController(ListProjectsUseCase listProjectsUseCase,
                             GetProjectUseCase getProjectUseCase,
                             CreateProjectUseCase createProjectUseCase,
                             UpdateProjectUseCase updateProjectUseCase) {
        this.listProjectsUseCase = listProjectsUseCase;
        this.getProjectUseCase = getProjectUseCase;
        this.createProjectUseCase = createProjectUseCase;
        this.updateProjectUseCase = updateProjectUseCase;
    }

    @GetMapping
    public ProjectListOutDto listProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long sourceId,
            @RequestParam(required = false) BigDecimal minScore,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "-score") String sort) {
        Page<Project> result = listProjectsUseCase.listProjects(page, pageSize, status, sourceId, minScore, search, sort);
        List<ProjectOutDto> items = result.getContent().stream()
            .map(this::toOutDto)
            .toList();
        return new ProjectListOutDto(items, (int) result.getTotalElements(), page, pageSize);
    }

    @GetMapping("/{id}")
    public ProjectOutDto getProject(@PathVariable Long id) {
        return toOutDto(getProjectUseCase.getProject(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectOutDto createProject(@Valid @RequestBody ProjectCreateDto dto) {
        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setUrl(dto.getUrl());
        project.setBudgetMin(dto.getBudgetMin());
        project.setBudgetMax(dto.getBudgetMax());
        project.setBudgetCurrency(dto.getBudgetCurrency() != null ? dto.getBudgetCurrency() : "USD");
        if (dto.getBudgetType() != null) {
            project.setBudgetType(BudgetType.fromValue(dto.getBudgetType()));
        }
        project.setTimeline(dto.getTimeline());
        project.setExternalId(dto.getExternalId());
        project.setPostedAt(dto.getPostedAt());
        project.setDeadline(dto.getDeadline());
        project.setRawData(dto.getRawData());
        project.setFingerprint(dto.getFingerprint());

        return toOutDto(createProjectUseCase.createProject(project, dto.getSourceId(), dto.getContactId()));
    }

    @PatchMapping("/{id}")
    public ProjectOutDto updateProject(@PathVariable Long id, @RequestBody ProjectUpdateDto dto) {
        ProjectStatus status = dto.getStatus() != null ? ProjectStatus.fromValue(dto.getStatus()) : null;
        return toOutDto(updateProjectUseCase.updateProject(id, status, dto.getNotes(), dto.getContactId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        updateProjectUseCase.deleteProject(id);
    }

    @PostMapping("/{id}/rescore")
    public Map<String, Object> rescoreProject(@PathVariable Long id) {
        UpdateProjectUseCase.RescoreResult result = updateProjectUseCase.rescoreProject(id);
        return Map.of("score", result.score(), "breakdown", result.breakdown());
    }

    private ProjectOutDto toOutDto(Project p) {
        ProjectOutDto dto = new ProjectOutDto();
        dto.setId(p.getId());
        dto.setExternalId(p.getExternalId());
        dto.setSourceId(p.getSource() != null ? p.getSource().getId() : null);
        dto.setSourceName(p.getSource() != null ? p.getSource().getDisplayName() : null);
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setUrl(p.getUrl());
        dto.setBudgetMin(p.getBudgetMin());
        dto.setBudgetMax(p.getBudgetMax());
        dto.setBudgetCurrency(p.getBudgetCurrency());
        dto.setBudgetType(p.getBudgetType() != null ? p.getBudgetType().getValue() : null);
        dto.setTimeline(p.getTimeline());
        dto.setPostedAt(p.getPostedAt());
        dto.setDeadline(p.getDeadline());
        dto.setStatus(p.getStatus() != null ? p.getStatus().getValue() : null);
        dto.setScore(p.getScore());
        dto.setScoreBreakdown(p.getScoreBreakdown());
        dto.setAiSummary(p.getAiSummary());
        dto.setAiTags(p.getAiTags());
        dto.setDuplicate(p.isDuplicate());
        dto.setNotes(p.getNotes());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());

        if (p.getContact() != null) {
            dto.setContact(new ContactBriefDto(
                p.getContact().getId(),
                p.getContact().getName(),
                p.getContact().getCompany(),
                p.getContact().getEmail()
            ));
        }

        if (p.getSkills() != null) {
            dto.setSkills(p.getSkills().stream()
                .map(s -> new SkillOutDto(s.getId(), s.getName(), s.getDescription()))
                .toList());
        }

        return dto;
    }
}