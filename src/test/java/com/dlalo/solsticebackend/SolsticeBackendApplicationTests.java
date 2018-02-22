package com.dlalo.solsticebackend;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.dlalo.solsticebackend.model.Address;
import com.dlalo.solsticebackend.model.Contact;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SolsticeBackendApplicationTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	public static final String REST_SERVICE_URI = "http://localhost:8080/api";

	@Test
	public void aaTestDeleteNotExistingContact() throws Exception {
		String notExistingId = "12312";
		mockMvc.perform(delete("/api/contacts/" + notExistingId)
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isNotFound())
	      .andExpect(jsonPath("$.message", is("Contact with ID => " +  notExistingId + " does not exist")));
	}
	
	@Test
	public void abTestCreateNewContact() throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/contacts";
    	Long birthDate = Instant.from(LocalDate.of(1979, Month.JUNE, 19).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	Address address = new Address("Buenos Aires", "Fake street", 123, "BA");
    	Contact contact = new Contact("Diego Lalo", "ProKarma", "http://myprofileimage.com", 
    			"fake@fakeserver.com", birthDate, "123456", "7890124", address);
    	URI contactUri = restTemplate.postForLocation(url, contact, Contact.class);
		// Retrieve created contact
		contact = restTemplate.getForObject(contactUri, Contact.class);
		// Check contents
		assertTrue(contact != null);
		assertTrue(contact.getAddress() != null);
		assertTrue(contact.getAddress().getCity().equals("Buenos Aires"));
		assertTrue(contact.getAddress().getStreetName().equals("Fake street"));
		assertTrue(contact.getAddress().getStreetNumber().equals(123));
		assertTrue(contact.getAddress().getStateCd().equals("BA"));
		assertTrue(contact.getName().equals("Diego Lalo"));
		assertTrue(contact.getCompany().equals("ProKarma"));
		assertTrue(contact.getProfileImage().equals("http://myprofileimage.com"));
		assertTrue(contact.getEmail().equals("fake@fakeserver.com"));
		assertTrue(contact.getBirthDate().equals(birthDate));
		assertTrue(contact.getPersonalphone().equals("123456"));
		assertTrue(contact.getWorkphone().equals("7890124"));
		restTemplate.delete(contactUri);
	}
	
	@Test
	public void acTestUpdateContact() throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/contacts";

    	// Create contact
    	Long birthDate = Instant.from(LocalDate.of(1979, Month.JUNE, 19).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	Address address = new Address("Buenos Aires", "Fake street", 123, "BA");
    	Contact contact = new Contact("Diego Lalo", "ProKarma", "http://myprofileimage.com", 
    			"fake@fakeserver.com", birthDate, "123456", "7890124", address);
    	URI contactUri = restTemplate.postForLocation(url, contact, Contact.class);
		
    	// Retrieve created contact
		contact = restTemplate.getForObject(contactUri, Contact.class);
		
		// Update contents
		contact.setName("FAKE NAME");
		contact.getAddress().setCity("SIN CITY");
		restTemplate.put(contactUri, contact);
		
		// Retrieve updated contact and check updated fields
		contact = restTemplate.getForObject(contactUri, Contact.class);
		assertTrue(contact != null);
		assertTrue(contact.getAddress() != null);
		assertTrue(contact.getAddress().getCity().equals("SIN CITY"));
		assertTrue(contact.getName().equals("FAKE NAME"));
		restTemplate.delete(contactUri);
	}
	
	@Test
	public void adTestDeleteContact() throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/contacts";

    	// Create contact
    	Long birthDate = Instant.from(LocalDate.of(1979, Month.JUNE, 19).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	Address address = new Address("Buenos Aires", "Fake street", 123, "BA");
    	Contact contact = new Contact("Homer Simpson", "Springfield Nuclear Power Plant", "http://myprofileimage.com", 
    			"fake@fakeserver.com", birthDate, "123456", "7890124", address);
    	URI contactUri = restTemplate.postForLocation(url, contact, Contact.class);
		
		// Delete contact
		restTemplate.delete(contactUri);
		
		// Try to retrieve deleted contact
		try {
			contact = restTemplate.getForObject(contactUri, Contact.class);
		} catch (HttpClientErrorException e) {
			assertTrue(e.getStatusCode().equals(HttpStatus.NOT_FOUND));
		}
	}
}
