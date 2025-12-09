package com.example.studentapi.dto;

public record StudentResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        int age
) {}