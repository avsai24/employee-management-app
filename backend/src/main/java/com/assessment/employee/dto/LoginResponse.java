package com.assessment.employee.dto;

public record LoginResponse(
        String token,
        String username,
        String role,
        long expiresInMs
) {
}
