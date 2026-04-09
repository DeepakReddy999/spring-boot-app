package com.example.customer.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.employeetaskmanagement.EmployeeTaskManagementApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = EmployeeTaskManagementApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllCustomers_returnsUnauthorizedWhenJwtTokenIsMissing() throws Exception {
        mockMvc.perform(get("/customers"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.error").value("Unauthorized"))
            .andExpect(jsonPath("$.message").value("Authentication is required to access this resource."))
            .andExpect(jsonPath("$.path").value("/customers"));
    }

    @Test
    void createCustomer_returnsConflictWhenEmailAlreadyExists() throws Exception {
        String accessToken = loginAndGetAccessToken("admin@company.com", "Admin@123");
        String customerEmail = "duplicate." + UUID.randomUUID() + "@example.com";
        String requestBody = """
            {
              "firstName": "Jordan",
              "lastName": "Lee",
              "email": "%s",
              "phone": "+1 555-0100"
            }
            """.formatted(customerEmail);

        mockMvc.perform(
            post("/customers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(customerEmail));

        mockMvc.perform(
            post("/customers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"))
            .andExpect(jsonPath("$.message").value("A customer with this email already exists."))
            .andExpect(jsonPath("$.path").value("/customers"));
    }

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "%s",
                      "password": "%s"
                    }
                    """.formatted(email, password))
        )
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode responseJson = objectMapper.readTree(responseBody);
        return responseJson.get("accessToken").asText();
    }
}
