package com.apus.salehub.domain.exception;

import com.apus.base.response.ErrorCode;

public class SkillErrorCodes {
    public static final ErrorCode SKILL_NOT_FOUND = new ErrorCode(404, "skill.not_found");

    private SkillErrorCodes() {
    }
}
