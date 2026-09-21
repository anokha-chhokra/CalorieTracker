package org.belex.backend.dto;

import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank
            @Size(min =2, max=80)
            String displayName,

            @NotBlank
            @Email
            @Size(max = 180)
            String email,

            @NotBlank
            @Size(min = 8, max = 20)
            String password
    ) {}

    public record LoginRequest(
            @NotBlank
            @Email
            String email,

            @NotBlank
            String password
    ) {}

    public record AuthResponse(
            String token,
            UserResponse user
    ) {}

    public record UserResponse(
            Long id,
            String email,
            String displayName,
            Integer dailyGoal
    ) {}

    public record ProfileUpdateRequest(
            @NotBlank
            @Size(min = 2, max = 80)
            String displayName,

            @Min(500)
            @Max(10000)
            Integer dailyGoal
    ) {}
}
