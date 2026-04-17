package com.apus.salehub.application.service;

import com.apus.salehub.application.port.in.source.SourceUseCase;
import com.apus.salehub.application.port.out.persistence.SourceRepository;
import com.apus.salehub.domain.model.Source;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional
public class SourceService implements SourceUseCase {

    private final SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Transactional(readOnly = true)
    public List<Source> listSources() {
        return sourceRepository.findAllByOrderByNameAsc();
    }

    public Source updateSource(Long id, Boolean isActive, Map<String, Object> configJson) {
        Source source = sourceRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Source not found: " + id));

        if (isActive != null) source.setActive(isActive);
        if (configJson != null) source.setConfigJson(configJson);

        return sourceRepository.save(source);
    }
}