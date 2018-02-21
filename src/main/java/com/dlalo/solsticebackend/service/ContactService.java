package com.dlalo.solsticebackend.service;

import java.util.List;

import com.dlalo.solsticebackend.model.Contact;

public interface ContactService {

	boolean existsContact(Long id);

	Contact getContact(Long id);

	List<Contact> getAllContacts();

	void createContact(Contact contact);

}
