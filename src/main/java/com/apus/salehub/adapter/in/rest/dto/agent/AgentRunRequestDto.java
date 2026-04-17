package com.apus.salehub.adapter.in.rest.dto.agent;

import java.util.ArrayList;
import java.util.List;

public class AgentRunRequestDto {
    private List<String> sources = new ArrayList<>();
    private List<String> keywords = new ArrayList<>();

    public List<String> getSources() { return sources; }
    public void setSources(List<String> sources) { this.sources = sources; }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
}
