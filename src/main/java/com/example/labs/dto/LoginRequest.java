package com.example.labs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    public String email;

    @NotBlank(message = "password is required")
    public String password;
}
