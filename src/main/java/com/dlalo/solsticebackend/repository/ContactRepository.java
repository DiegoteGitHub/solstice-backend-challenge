package com.dlalo.solsticebackend.repository;

import org.springframework.data.repository.CrudRepository;

import com.dlalo.solsticebackend.model.Contact;

public interface ContactRepository extends CrudRepository<Contact, Long> {

}
