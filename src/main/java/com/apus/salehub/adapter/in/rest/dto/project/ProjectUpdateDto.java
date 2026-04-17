package com.apus.salehub.adapter.in.rest.dto.project;

public class ProjectUpdateDto {

    private String status;
    private String notes;
    private Long contactId;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getContactId() { return contactId; }
    public void setContactId(Long contactId) { this.contactId = contactId; }
}
