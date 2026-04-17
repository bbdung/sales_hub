package com.apus.salehub.application.service;

import com.apus.salehub.application.port.out.persistence.ProjectRepository;
import com.apus.salehub.domain.model.Project;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DedupService {

    private final ProjectRepository projectRepository;

    public DedupService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Long> checkDuplicate(String title, String description, String sourceName) {
        String fingerprint = ScoringService.computeFingerprint(title, description, sourceName);
        return projectRepository.findByFingerprintAndIsDuplicateFalse(fingerprint)
            .map(Project::getId);
    }

    public void markDuplicate(Project project, Long originalId) {
        project.setDuplicate(true);
        project.setDuplicateOfId(originalId);
        projectRepository.save(project);
    }
}
