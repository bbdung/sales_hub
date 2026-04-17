package com.apus.salehub.application.service;

import com.apus.salehub.application.port.in.contact.ContactUseCase;
import com.apus.salehub.application.port.out.persistence.ContactRepository;
import com.apus.salehub.domain.model.Contact;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ContactService implements ContactUseCase {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Transactional(readOnly = true)
    public List<Contact> listContacts() {
        return contactRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Contact getContact(Long id) {
        return contactRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Contact not found: " + id));
    }

    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public Contact updateContact(Long id, Contact updated) {
        Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Contact not found: " + id));

        if (updated.getName() != null) contact.setName(updated.getName());
        if (updated.getEmail() != null) contact.setEmail(updated.getEmail());
        if (updated.getPhone() != null) contact.setPhone(updated.getPhone());
        if (updated.getCompany() != null) contact.setCompany(updated.getCompany());
        if (updated.getLinkedinUrl() != null) contact.setLinkedinUrl(updated.getLinkedinUrl());
        if (updated.getProfileUrl() != null) contact.setProfileUrl(updated.getProfileUrl());
        if (updated.getLocation() != null) contact.setLocation(updated.getLocation());
        if (updated.getNotes() != null) contact.setNotes(updated.getNotes());

        return contactRepository.save(contact);
    }
}