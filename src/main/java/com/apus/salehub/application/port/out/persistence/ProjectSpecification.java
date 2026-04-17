package com.apus.salehub.application.port.out.persistence;

import com.apus.salehub.domain.model.Project;
import com.apus.salehub.domain.model.enums.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProjectSpecification {

    private ProjectSpecification() {}

    public static Specification<Project> hasStatus(ProjectStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Project> hasSourceId(Long sourceId) {
        return (root, query, cb) -> cb.equal(root.get("source").get("id"), sourceId);
    }

    public static Specification<Project> hasMinScore(BigDecimal minScore) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("score"), minScore);
    }

    public static Specification<Project> searchText(String search) {
        return (root, query, cb) -> {
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}
