package com.example.labs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username must be 3-50 chars")
    public String username;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    public String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 72, message = "password must be 8-72 chars")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
            message = "password must contain at least 1 uppercase letter and 1 digit"
    )
    public String password;
}