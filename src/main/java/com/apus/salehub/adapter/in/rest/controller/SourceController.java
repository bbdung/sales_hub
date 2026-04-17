package com.apus.salehub.adapter.in.rest.controller;

import com.apus.base.response.ResponseWrapper;
import com.apus.salehub.adapter.in.rest.dto.source.SourceOutDto;
import com.apus.salehub.adapter.in.rest.dto.source.SourceUpdateDto;
import com.apus.salehub.application.port.in.source.SourceUseCase;
import com.apus.salehub.domain.model.Source;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sources")
@Tag(name = "Source", description = "Source management operations")
@ResponseWrapper
public class SourceController {

    private final SourceUseCase sourceUseCase;

    public SourceController(SourceUseCase sourceUseCase) {
        this.sourceUseCase = sourceUseCase;
    }

    @GetMapping
    public List<SourceOutDto> listSources() {
        return sourceUseCase.listSources().stream()
            .map(this::toOutDto)
            .toList();
    }

    @PatchMapping("/{id}")
    public SourceOutDto updateSource(@PathVariable Long id, @RequestBody SourceUpdateDto dto) {
        return toOutDto(sourceUseCase.updateSource(id, dto.getIsActive(), dto.getConfigJson()));
    }

    private SourceOutDto toOutDto(Source s) {
        return new SourceOutDto(
            s.getId(), s.getName(), s.getDisplayName(),
            s.getBaseUrl(), s.isActive(), s.getConfigJson(), s.getCreatedAt()
        );
    }
}