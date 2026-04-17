package com.apus.salehub.adapter.in.rest.dto.skill;

import jakarta.validation.constraints.NotBlank;

public class SkillCreateDto {

    @NotBlank
    private String name;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}