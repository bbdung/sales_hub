package com.apus.salehub.application.port.in.skill;

import com.apus.salehub.domain.model.Skill;

import java.util.List;

public interface SkillUseCase {

    List<Skill> listSkills();

    Skill getSkill(Long id);

    Skill createSkill(Skill skill);

    Skill updateSkill(Long id, Skill updated);

    void deleteSkill(Long id);
}