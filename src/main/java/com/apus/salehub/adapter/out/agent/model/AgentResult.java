package com.apus.salehub.adapter.out.agent.model;

import java.util.ArrayList;
import java.util.List;

public class AgentResult {

    private String sourceName;
    private int totalFound = 0;
    private int newProjects = 0;
    private int duplicates = 0;
    private List<String> errors = new ArrayList<>();

    public AgentResult(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public int getTotalFound() { return totalFound; }
    public void setTotalFound(int totalFound) { this.totalFound = totalFound; }

    public int getNewProjects() { return newProjects; }
    public void setNewProjects(int newProjects) { this.newProjects = newProjects; }

    public int getDuplicates() { return duplicates; }
    public void setDuplicates(int duplicates) { this.duplicates = duplicates; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
