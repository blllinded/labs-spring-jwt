package com.example.labs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskUpdateRequest {

    @NotBlank(message = "title is required")
    @Size(max = 200, message = "title max 200 chars")
    public String title;

    @Size(max = 1000, message = "description max 1000 chars")
    public String description;
}
