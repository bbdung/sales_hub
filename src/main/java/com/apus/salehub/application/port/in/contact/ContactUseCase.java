package com.apus.salehub.application.port.in.contact;

import com.apus.salehub.domain.model.Contact;

import java.util.List;

public interface ContactUseCase {

    List<Contact> listContacts();

    Contact getContact(Long id);

    Contact createContact(Contact contact);

    Contact updateContact(Long id, Contact updated);
}