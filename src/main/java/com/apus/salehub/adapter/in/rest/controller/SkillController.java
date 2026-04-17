package com.apus.salehub.adapter.in.rest.controller;

import com.apus.base.response.ErrorCode;
import com.apus.base.response.ResponseWrapper;
import com.apus.salehub.adapter.in.rest.dto.project.SkillOutDto;
import com.apus.salehub.adapter.in.rest.dto.skill.SkillCreateDto;
import com.apus.salehub.adapter.in.rest.dto.skill.SkillUpdateDto;
import com.apus.salehub.application.port.in.skill.SkillUseCase;
import com.apus.salehub.domain.model.Skill;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skill", description = "Skill management operations")
@ResponseWrapper
public class SkillController {

    private final SkillUseCase skillUseCase;

    public SkillController(SkillUseCase skillUseCase) {
        this.skillUseCase = skillUseCase;
    }

    @GetMapping("/list")
    @Operation(
            summary = "List all skills",
            description = "Returns all available skills.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skills retrieved")
            }
    )
    public List<SkillOutDto> listSkills() {
        return skillUseCase.listSkills().stream()
            .map(this::toOutDto)
            .toList();
    }

    @GetMapping()
    @Operation(
            summary = "Get skill by ID",
            description = "Returns a single skill by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skill retrieved"),
                    @ApiResponse(responseCode = "404", description = "Skill not found",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class)))
            }
    )
    public SkillOutDto getSkill(@RequestParam Long id) {
        return toOutDto(skillUseCase.getSkill(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a skill",
            description = "Creates a new skill. Name is required and must be unique.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Skill created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class)))
            }
    )
    public SkillOutDto createSkill(@Valid @RequestBody SkillCreateDto dto) {
        Skill skill = new Skill();
        skill.setName(dto.getName());
        skill.setDescription(dto.getDescription());
        return toOutDto(skillUseCase.createSkill(skill));
    }

    @PatchMapping()
    @Operation(
            summary = "Update a skill",
            description = "Partially updates an existing skill.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skill updated"),
                    @ApiResponse(responseCode = "404", description = "Skill not found",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class)))
            }
    )
    public SkillOutDto updateSkill(@RequestParam Long id, @Valid @RequestBody SkillUpdateDto dto) {
        Skill updated = new Skill();
        if (dto.getName() != null) updated.setName(dto.getName());
        if (dto.getDescription() != null) updated.setDescription(dto.getDescription());
        return toOutDto(skillUseCase.updateSkill(id, updated));
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a skill",
            description = "Deletes a skill by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Skill deleted"),
                    @ApiResponse(responseCode = "404", description = "Skill not found",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class)))
            }
    )
    public void deleteSkill(@RequestParam Long id) {
        skillUseCase.deleteSkill(id);
    }

    private SkillOutDto toOutDto(Skill s) {
        return new SkillOutDto(s.getId(), s.getName(), s.getDescription());
    }
}