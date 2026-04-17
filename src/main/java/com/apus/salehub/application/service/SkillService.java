package com.apus.salehub.application.service;

import com.apus.base.exception.CommandException;
import com.apus.base.exception.CommandExceptionBuilder;
import com.apus.salehub.application.port.in.skill.SkillUseCase;
import com.apus.salehub.application.port.out.persistence.SkillRepository;
import com.apus.salehub.domain.exception.SkillErrorCodes;
import com.apus.salehub.domain.model.Skill;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class SkillService implements SkillUseCase {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<Skill> listSkills() {
        return skillRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Skill getSkill(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> CommandExceptionBuilder.exception(SkillErrorCodes.SKILL_NOT_FOUND));
    }

    public Skill createSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Long id, Skill updated) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> CommandExceptionBuilder.exception(SkillErrorCodes.SKILL_NOT_FOUND));

        if (updated.getName() != null) skill.setName(updated.getName());
        if (updated.getDescription() != null) skill.setDescription(updated.getDescription());

        return skillRepository.save(skill);
    }

    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw CommandExceptionBuilder.exception(SkillErrorCodes.SKILL_NOT_FOUND);
        }
        skillRepository.deleteById(id);
    }
}