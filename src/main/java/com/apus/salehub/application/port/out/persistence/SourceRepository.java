package com.apus.salehub.application.port.out.persistence;

import com.apus.salehub.domain.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SourceRepository extends JpaRepository<Source, Long> {

    Optional<Source> findByName(String name);

    List<Source> findByIsActiveTrue();

    List<Source> findByNameIn(List<String> names);

    List<Source> findAllByOrderByNameAsc();
}
