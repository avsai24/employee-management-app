package com.assessment.employee.dto;

public record LoginResponse(
        String token,
        String refreshToken,
        String username,
        String role,
        long expiresInMs
) {
}
