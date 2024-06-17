package com.adorsys.gis.powerpay.powerpaybackend.services_tests;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.adorsys.gis.powerpay.powerpaybackend.services.userRegistrationController.CompletionRequest;
import com.adorsys.gis.powerpay.powerpaybackend.services.userRegistrationController.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegisterUser() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setPhoneNumber("1234567890");
        request.setUserName("testuser");

        mockMvc.perform(post("/api/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.userName").value("testuser"));
    }

    @Test
    public void testCompleteRegistration() throws Exception {
        // Register a user
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setPhoneNumber("1234567890");
        registrationRequest.setUserName("testuser");

        mockMvc.perform(post("/api/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Complete the registration
        CompletionRequest completionRequest = new CompletionRequest();
        completionRequest.setPin("1234");
        completionRequest.setOtp("5678");

        mockMvc.perform(post("/api/registration/{registrationId}", 1) // Assuming registrationId is 1
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.userName").value("testuser"));
    }
}

