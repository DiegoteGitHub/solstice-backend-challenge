package com.dlalo.solsticebackend.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlalo.solsticebackend.model.Contact;
import com.dlalo.solsticebackend.repository.ContactRepository;

@Service("contactService")
public class ContactServiceImpl implements ContactService {
	
	@Autowired
	ContactRepository contactRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

	@Override
	public boolean existsContact(Long id) {
		return contactRepository.findById(id).isPresent();
	}

	@Override
	public Contact getContact(Long id) {
		return contactRepository.findById(id).get();
	}

	@Override
	public List<Contact> getAllContacts() {
		List<Contact> contacts = new ArrayList<Contact>();
		for(Contact c: contactRepository.findAll()) {
			contacts.add(c);
		}
		return contacts;
	}

	@Override
	public void createContact(Contact contact) {
		contactRepository.save(contact);
	}

}
