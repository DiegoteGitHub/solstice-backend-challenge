package com.dlalo.solsticebackend.service;

import java.util.List;

import com.dlalo.solsticebackend.model.Contact;

public interface ContactService {

	boolean existsContact(Long id);

	Contact getContact(Long id);

	List<Contact> getAllContacts(String email, String phoneNumber, String state, String city);

	Contact createContact(Contact contact);

	Contact updateContact(Contact contact, Long id);

	void deleteContact(Long id);

}
