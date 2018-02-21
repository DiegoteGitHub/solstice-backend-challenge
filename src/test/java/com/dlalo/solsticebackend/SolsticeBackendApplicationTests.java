package com.dlalo.solsticebackend;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc 
public class SolsticeBackendApplicationTests {
	
	@Autowired
	private MockMvc mvc;

	@Test
	public void aaTestDeleteNotExistingContact() throws Exception {
		String notExistingId = "12312";
		mvc.perform(delete("/api/contacts/" + notExistingId)
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isNotFound())
	      .andExpect(jsonPath("$.message", is("Contact with ID => " +  notExistingId + " does not exist")));
	}

}
