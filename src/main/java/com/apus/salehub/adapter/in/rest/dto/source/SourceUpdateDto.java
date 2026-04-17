package com.apus.salehub.adapter.in.rest.dto.source;

import java.util.Map;

public class SourceUpdateDto {
    private Boolean isActive;
    private Map<String, Object> configJson;

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Map<String, Object> getConfigJson() { return configJson; }
    public void setConfigJson(Map<String, Object> configJson) { this.configJson = configJson; }
}
