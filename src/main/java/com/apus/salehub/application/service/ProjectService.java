package com.apus.salehub.application.service;

import com.apus.salehub.application.port.in.project.CreateProjectUseCase;
import com.apus.salehub.application.port.in.project.GetProjectUseCase;
import com.apus.salehub.application.port.in.project.ListProjectsUseCase;
import com.apus.salehub.application.port.in.project.UpdateProjectUseCase;
import com.apus.salehub.application.port.out.persistence.ContactRepository;
import com.apus.salehub.application.port.out.persistence.ProjectRepository;
import com.apus.salehub.application.port.out.persistence.ProjectSpecification;
import com.apus.salehub.application.port.out.persistence.SourceRepository;
import com.apus.salehub.domain.model.Project;
import com.apus.salehub.domain.model.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProjectService implements ListProjectsUseCase, GetProjectUseCase, CreateProjectUseCase, UpdateProjectUseCase {

    private final ProjectRepository projectRepository;
    private final SourceRepository sourceRepository;
    private final ContactRepository contactRepository;
    private final ScoringService scoringService;

    public ProjectService(ProjectRepository projectRepository,
                          SourceRepository sourceRepository,
                          ContactRepository contactRepository,
                          ScoringService scoringService) {
        this.projectRepository = projectRepository;
        this.sourceRepository = sourceRepository;
        this.contactRepository = contactRepository;
        this.scoringService = scoringService;
    }

    @Transactional(readOnly = true)
    public Page<Project> listProjects(int page, int pageSize, String status,
                                      Long sourceId, BigDecimal minScore,
                                      String search, String sort) {
        Specification<Project> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        if (status != null) spec = spec.and(ProjectSpecification.hasStatus(ProjectStatus.fromValue(status)));
        if (sourceId != null) spec = spec.and(ProjectSpecification.hasSourceId(sourceId));
        if (minScore != null) spec = spec.and(ProjectSpecification.hasMinScore(minScore));
        if (search != null) spec = spec.and(ProjectSpecification.searchText(search));

        Sort jpaSort = parseSort(sort);
        PageRequest pageable = PageRequest.of(page - 1, pageSize, jpaSort);

        return projectRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Project getProject(Long id) {
        return projectRepository.findWithRelationsById(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found: " + id));
    }

    public Project createProject(Project project, Long sourceId, Long contactId) {
        if (sourceId != null) {
            project.setSource(sourceRepository.findById(sourceId)
                .orElseThrow(() -> new NoSuchElementException("Source not found: " + sourceId)));
        }
        if (contactId != null) {
            project.setContact(contactRepository.findById(contactId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found: " + contactId)));
        }
        return projectRepository.save(project);
    }

    public Project updateProject(Long id, ProjectStatus status, String notes, Long contactId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found: " + id));

        if (status != null) project.setStatus(status);
        if (notes != null) project.setNotes(notes);
        if (contactId != null) {
            project.setContact(contactRepository.findById(contactId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found: " + contactId)));
        }

        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found: " + id));
        project.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(project);
    }

    public RescoreResult rescoreProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found: " + id));

        ScoringService.ScoreResult result = scoringService.scoreProject(project);
        project.setScore(result.score());
        project.setScoreBreakdown(result.breakdown());
        projectRepository.save(project);
        return new RescoreResult(result.score(), result.breakdown());
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isEmpty()) sort = "-score";
        boolean desc = sort.startsWith("-");
        String field = desc ? sort.substring(1) : sort;

        String entityField = switch (field) {
            case "score" -> "score";
            case "created_at" -> "createdAt";
            case "posted_at" -> "postedAt";
            default -> "score";
        };

        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(new Sort.Order(direction, entityField).nullsLast());
    }
}