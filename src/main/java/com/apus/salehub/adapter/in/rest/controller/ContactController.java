package com.apus.salehub.adapter.in.rest.controller;

import com.apus.base.response.ResponseWrapper;
import com.apus.salehub.adapter.in.rest.dto.contact.ContactCreateDto;
import com.apus.salehub.adapter.in.rest.dto.contact.ContactOutDto;
import com.apus.salehub.adapter.in.rest.dto.contact.ContactUpdateDto;
import com.apus.salehub.application.port.in.contact.ContactUseCase;
import com.apus.salehub.domain.model.Contact;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contact", description = "Contact management operations")
@ResponseWrapper
public class ContactController {

    private final ContactUseCase contactUseCase;

    public ContactController(ContactUseCase contactUseCase) {
        this.contactUseCase = contactUseCase;
    }

    @GetMapping
    public List<ContactOutDto> listContacts() {
        return contactUseCase.listContacts().stream()
            .map(this::toOutDto)
            .toList();
    }

    @GetMapping("/{id}")
    public ContactOutDto getContact(@PathVariable Long id) {
        return toOutDto(contactUseCase.getContact(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactOutDto createContact(@RequestBody ContactCreateDto dto) {
        Contact contact = new Contact();
        applyDto(contact, dto);
        return toOutDto(contactUseCase.createContact(contact));
    }

    @PatchMapping("/{id}")
    public ContactOutDto updateContact(@PathVariable Long id, @RequestBody ContactUpdateDto dto) {
        Contact updated = new Contact();
        applyDto(updated, dto);
        return toOutDto(contactUseCase.updateContact(id, updated));
    }

    private void applyDto(Contact contact, ContactCreateDto dto) {
        if (dto.getName() != null) contact.setName(dto.getName());
        if (dto.getEmail() != null) contact.setEmail(dto.getEmail());
        if (dto.getPhone() != null) contact.setPhone(dto.getPhone());
        if (dto.getCompany() != null) contact.setCompany(dto.getCompany());
        if (dto.getLinkedinUrl() != null) contact.setLinkedinUrl(dto.getLinkedinUrl());
        if (dto.getProfileUrl() != null) contact.setProfileUrl(dto.getProfileUrl());
        if (dto.getLocation() != null) contact.setLocation(dto.getLocation());
        if (dto.getNotes() != null) contact.setNotes(dto.getNotes());
    }

    private ContactOutDto toOutDto(Contact c) {
        return new ContactOutDto(
            c.getId(), c.getName(), c.getEmail(), c.getPhone(),
            c.getCompany(), c.getLinkedinUrl(), c.getProfileUrl(),
            c.getLocation(), c.getNotes(), c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}