package com.example.labs.controller;

import com.example.labs.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestValidationController {

    @PostMapping("/register")
    public String test(@Valid @RequestBody RegisterRequest req) {
        return "OK";
    }
}
