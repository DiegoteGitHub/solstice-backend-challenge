package com.dlalo.solsticebackend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.dlalo.solsticebackend.model.Contact;
import com.dlalo.solsticebackend.service.ContactService;
import com.dlalo.solsticebackend.util.CustomMessageType;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
	
	private static final Logger logger = LoggerFactory.getLogger(ContactController.class);
	
	@Autowired
	ContactService service;
	
	@RequestMapping(method = RequestMethod.GET, produces= { "application/json" })
	ResponseEntity<List<Contact>> getAllContacts(
			@RequestParam(value="email", required = false) String email, 
			@RequestParam(value="phoneNumber", required = false) String phoneNumber,
			@RequestParam(value="state", required = false) String state,
			@RequestParam(value="city", required = false) String city,
			UriComponentsBuilder ucBuilder) {
		
		List<Contact> contacts = service.getAllContacts(email, phoneNumber, state, city);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/contacts").build().toUri());
		return new ResponseEntity<List<Contact>>(contacts, headers, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/{id}" , method = RequestMethod.GET, produces= { "application/json" })
	public ResponseEntity<?> getContact(@PathVariable(value="id") Long id, UriComponentsBuilder ucBuilder) {
		
		HttpHeaders headers = new HttpHeaders();
		if (!service.existsContact(id)) {
			headers.setLocation(ucBuilder.path("/api/contacts/{id}").buildAndExpand(id).toUri());
        	CustomMessageType message =  new CustomMessageType("Contact with ID => " + id + " does not exist");
        	return new ResponseEntity<CustomMessageType>(message, headers, HttpStatus.NOT_FOUND);
		} else {
			Contact contact = service.getContact(id);
			headers.setLocation(ucBuilder.path("/api/contacts/{id}").buildAndExpand(contact.getId()).toUri());
			return new ResponseEntity<Contact>(contact, headers, HttpStatus.OK);
		}
	}
	
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addContact(@RequestBody Contact contact, UriComponentsBuilder ucBuilder) {
        
        HttpHeaders headers = new HttpHeaders();
        try {
        	contact = service.createContact(contact);
        	logger.debug("Created contact with ID => " + contact.getId());
        	headers.setLocation(ucBuilder.path("/api/contacts/{id}").buildAndExpand(contact.getId()).toUri());
            return new ResponseEntity<Contact>(contact, headers, HttpStatus.CREATED);
        } catch (Exception e) {
        	String errStr = "Unable to create contact, cause => " + e.getMessage();
        	logger.error(errStr);
        	CustomMessageType error =  new CustomMessageType(errStr);
        	return new ResponseEntity<CustomMessageType>(error, headers, HttpStatus.NOT_FOUND);
        }
    }
	
	@RequestMapping(path = "/{id}" , method = RequestMethod.PUT, produces= { "application/json" })
	public ResponseEntity<?> updateContact(@PathVariable(value="id") Long id, @RequestBody Contact contact, UriComponentsBuilder ucBuilder) {
		
		HttpHeaders headers = new HttpHeaders();
		if (!service.existsContact(id)) {
			headers.setLocation(ucBuilder.path("/api/contacts/{id}").buildAndExpand(id).toUri());
        	CustomMessageType message =  new CustomMessageType("Contact with ID => " + id + " does not exist");
        	return new ResponseEntity<CustomMessageType>(message, headers, HttpStatus.NOT_FOUND);
		} else {
			contact = service.updateContact(contact, id);
			headers.setLocation(ucBuilder.path("/api/contacts/{id}").buildAndExpand(contact.getId()).toUri());
			return new ResponseEntity<Contact>(contact, headers, HttpStatus.OK);
		}
	}
	
	@RequestMapping(path = "/{id}" , method = RequestMethod.DELETE, produces= { "application/json" })
	public ResponseEntity<?> deleteContact(@PathVariable(value="id") Long id, UriComponentsBuilder ucBuilder) {
		
		HttpHeaders headers = new HttpHeaders();
		if (!service.existsContact(id)) {
			headers.setLocation(ucBuilder.path("/api/contacts/{id}").buildAndExpand(id).toUri());
        	CustomMessageType message =  new CustomMessageType("Contact with ID => " + id + " does not exist");
        	return new ResponseEntity<CustomMessageType>(message, headers, HttpStatus.NOT_FOUND);
		} else {
			service.deleteContact(id);
			CustomMessageType message =  new CustomMessageType("Contact with ID => " + id + " DELETED");
			headers.setLocation(ucBuilder.path("/api/contacts/{id}").buildAndExpand(id).toUri());
			return new ResponseEntity<CustomMessageType>(message, headers, HttpStatus.OK);
		}
	}
}
