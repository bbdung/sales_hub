package com.apus.salehub.application.port.out.persistence;

import com.apus.salehub.domain.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findAllByOrderByCreatedAtDesc();
}
