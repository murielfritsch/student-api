package com.example.studentapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StudentRequestDto(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Email @NotBlank String email,
            @Min(16) int age
    ) {}
