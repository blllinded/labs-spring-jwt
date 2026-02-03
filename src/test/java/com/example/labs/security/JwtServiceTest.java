package com.example.labs.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldGenerateAndValidateAccessToken() {
        String email = "test@example.com";

        String token = jwtService.generateAccessToken(email);

        assertNotNull(token);
        assertTrue(jwtService.isValid(token));
        assertEquals(email, jwtService.extractEmail(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertFalse(jwtService.isValid("abc.def.ghi"));
    }
}
