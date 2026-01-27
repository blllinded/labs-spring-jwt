package com.example.labs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username must be 3-50 chars")
    public String username;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    public String email;

    @NotBlank(message = "password is required")
    public String password;
}
