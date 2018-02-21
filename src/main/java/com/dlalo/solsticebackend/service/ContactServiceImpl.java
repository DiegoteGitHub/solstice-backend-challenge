package com.dlalo.solsticebackend.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dlalo.solsticebackend.model.Contact;
import com.dlalo.solsticebackend.repository.AddressRepository;
import com.dlalo.solsticebackend.repository.ContactRepository;

@Service("contactService")
public class ContactServiceImpl implements ContactService {
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	AddressRepository addressRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);
	
	@PersistenceContext
	EntityManager entityManager;

	@Override
	public boolean existsContact(Long id) {
		return contactRepository.findById(id).isPresent();
	}

	@Override
	public Contact getContact(Long id) {
		return contactRepository.findById(id).get();
	}

	@Override
	public List<Contact> getAllContacts(String email, String phoneNumber, String state, String city) {
		List<Contact> contacts = new ArrayList<Contact>();
		if (allFiltersNull(email, phoneNumber, state, city)) {
			for(Contact c: contactRepository.findAll()) {
				contacts.add(c);
			}
		} else {
			TypedQuery<Contact> tQuery = entityManager.createQuery(buildQuery(email, phoneNumber), Contact.class);
			if (!StringUtils.isEmpty(email))
				tQuery.setParameter("email", email);
			if (!StringUtils.isEmpty(phoneNumber))
				tQuery.setParameter("phoneNumber", phoneNumber);
			for (Contact c: tQuery.getResultList()) {
				if (StringUtils.isEmpty(state) && StringUtils.isEmpty(city))
					contacts.add(c);
				else {
					if (!StringUtils.isEmpty(state) && !StringUtils.isEmpty(city)) {
						if (c.getAddress().getStateCd().equals(state) && c.getAddress().getCity().equals(city))
							contacts.add(c);
					} else {
						if (!StringUtils.isEmpty(state) && c.getAddress().getStateCd().equals(state))
							contacts.add(c);
						if (!StringUtils.isEmpty(city) && c.getAddress().getCity().equals(city))
							contacts.add(c);
					}
				}
			}
		}
		return contacts;
	}

	@Override
	public Contact createContact(Contact contact) {
		if (contact.getAddress() != null)
			addressRepository.save(contact.getAddress());
		return contactRepository.save(contact);
	}

	@Override
	public Contact updateContact(Contact contact, Long id) {
		Contact oldContact = contactRepository.findById(id).get();
		oldContact.setAddress(contact.getAddress());
		oldContact.setName(contact.getName());
		oldContact.setCompany(contact.getCompany());
		oldContact.setProfileImage(contact.getProfileImage());
		oldContact.setEmail(contact.getEmail());
		oldContact.setBirthDate(contact.getBirthDate());
		oldContact.setPersonalphone(contact.getPersonalphone());
		oldContact.setWorkphone(contact.getWorkphone());
		contactRepository.save(oldContact);
		return contactRepository.findById(id).get();
	}

	@Override
	public void deleteContact(Long id) {
		contactRepository.deleteById(id);
	}
	
	private boolean allFiltersNull(String email, String phoneNumber, String state, String city) {
		return StringUtils.isEmpty(email) && StringUtils.isEmpty(phoneNumber) 
				&& StringUtils.isEmpty(state) && StringUtils.isEmpty(city);
	}
	
	private String buildQuery(String email, String phoneNumber) {
		String qryStr = "SELECT c FROM Contact c WHERE 1 = 1";
		if (!StringUtils.isEmpty(email))
			qryStr += " AND c.email = :email";
		if (!StringUtils.isEmpty(phoneNumber))
			qryStr += " AND (c.personalphone = :phoneNumber OR c.workphone =  :phoneNumber)";
		return qryStr;
	}
}
