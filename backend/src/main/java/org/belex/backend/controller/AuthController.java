package org.belex.backend.controller;

import jakarta.validation.Valid;
import org.belex.backend.dto.AuthDtos;
import org.belex.backend.model.User;
import org.belex.backend.service.AuthService;
import org.belex.backend.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request){
        return authService.login(request);
    }

    @GetMapping("/me")
    public AuthDtos.UserResponse me(@AuthenticationPrincipal UserDetails principal){
        User user = userService.getFromPrincipal(principal);
        return new AuthDtos.UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getDailyGoal()
        );
    }
}
