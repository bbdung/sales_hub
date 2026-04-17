package com.apus.salehub.application.port.in.source;

import com.apus.salehub.domain.model.Source;

import java.util.List;
import java.util.Map;

public interface SourceUseCase {

    List<Source> listSources();

    Source updateSource(Long id, Boolean isActive, Map<String, Object> configJson);
}