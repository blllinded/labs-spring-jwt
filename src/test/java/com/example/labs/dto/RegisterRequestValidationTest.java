package com.example.labs.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestValidationTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldRejectInvalidEmail() {
        RegisterRequest req = new RegisterRequest();
        req.username = "john";
        req.email = "not-email";
        req.password = "Password123!";

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectTooShortUsername() {
        RegisterRequest req = new RegisterRequest();
        req.username = "ab"; // меньше 3
        req.email = "john@example.com";
        req.password = "Password123!";

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAcceptValidRequest() {
        RegisterRequest req = new RegisterRequest();
        req.username = "john";
        req.email = "john@example.com";
        req.password = "Password123!";

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }
}
