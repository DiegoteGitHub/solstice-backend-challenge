package com.dlalo.solsticebackend.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

import com.dlalo.solsticebackend.model.Address;
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
	public Contact createContact(Contact contact) throws RuntimeException {
		validateContactFields(contact);
		addressRepository.save(contact.getAddress());
		return contactRepository.save(contact);
	}

	@Override
	public Contact updateContact(Contact contact, Long id) throws RuntimeException {
		validateContactFields(contact);
		Contact oldContact = contactRepository.findById(id).get();
		oldContact.setAddress(contact.getAddress());
		oldContact.setName(contact.getName());
		oldContact.setCompany(contact.getCompany());
		oldContact.setProfileImage(contact.getProfileImage());
		oldContact.setEmail(contact.getEmail());
		oldContact.setBirthDate(contact.getBirthDate());
		oldContact.setPersonalphone(contact.getPersonalphone());
		oldContact.setWorkphone(contact.getWorkphone());
		addressRepository.save(oldContact.getAddress());
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
	
	private void validateContactFields(Contact contact) throws RuntimeException {
		if (StringUtils.isEmpty(contact.getName()))
			throw new RuntimeException("Name is mandatory");
		if (StringUtils.isEmpty(contact.getCompany()))
			throw new RuntimeException("Company is mandatory");
		if (StringUtils.isEmpty(contact.getProfileImage()))
			throw new RuntimeException("Profile image URL is mandatory");
		if (StringUtils.isEmpty(contact.getEmail()))
			throw new RuntimeException("Email is mandatory");
		if (StringUtils.isEmpty(contact.getBirthDate()))
			throw new RuntimeException("Birthdate is mandatory");
		else if (LocalDate.now().equals(Instant.ofEpochMilli(contact.getBirthDate()).atZone(ZoneId.systemDefault()).toLocalDate()) 
				|| Instant.ofEpochMilli(contact.getBirthDate()).atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now()))
			throw new RuntimeException("Birthdate cannot be today or after today");
		if (StringUtils.isEmpty(contact.getPersonalphone()))
			throw new RuntimeException("Personal phone is mandatory");
		if (StringUtils.isEmpty(contact.getWorkphone()))
			throw new RuntimeException("Work phone is mandatory");
		validateAddress(contact.getAddress());
	}

	private void validateAddress(Address address) throws RuntimeException {
		if (address == null)
			throw new RuntimeException("Address information is mandatory");
		else {
			if (StringUtils.isEmpty(address.getCity()))
				throw new RuntimeException("Address city is mandatory");
			if (StringUtils.isEmpty(address.getStateCd()))
				throw new RuntimeException("Address state is mandatory");
			if (StringUtils.isEmpty(address.getStreetName()))
				throw new RuntimeException("Address street name is mandatory");
			if (StringUtils.isEmpty(address.getStreetNumber()))
				throw new RuntimeException("Address street number is mandatory");
			
		}
	}
}
