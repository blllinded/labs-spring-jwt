package com.example.labs.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void secureShouldReturn403WithoutToken() throws Exception {
        mockMvc.perform(get("/secure"))
                .andExpect(status().isForbidden());
    }

    @Test
    void secureShouldReturn200WithValidToken() throws Exception {
        String email = "t_" + UUID.randomUUID() + "@example.com";
        String password = "Password123!";

        // register
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"john","email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isCreated());

        // login
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(loginResponse);
        String accessToken = json.get("accessToken").asText();

        // secure with token
        mockMvc.perform(get("/secure")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("YOU ARE AUTHENTICATED"));
    }

    @Test
    void secureShouldReturn403WithInvalidToken() throws Exception {
        mockMvc.perform(get("/secure")
                        .header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isForbidden());
    }

    @Test
    void secureShouldReturn403WithExpiredToken() throws Exception {
        String email = "t_" + UUID.randomUUID() + "@example.com";
        String password = "Password123!";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"john","email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isCreated());

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper
                .readTree(loginResponse)
                .get("accessToken")
                .asText();


        Thread.sleep(2500);

        mockMvc.perform(get("/secure")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }
}
