package org.belex.backend.service;

import org.belex.backend.dto.AuthDtos;
import org.belex.backend.model.User;
import org.belex.backend.repo.UserRepo;
import org.belex.backend.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest registerRequest){
        String email = registerRequest.email().trim().toLowerCase();
        if(userRepo.existsByEmailIgnoreCase(email)){
            throw new IllegalArgumentException("Email already exists");
        }

        User  user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setDisplayName(registerRequest.displayName().trim());
        user.setDailyGoal(2000);
        userRepo.save(user);

        return responseFor(user);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest loginRequest){
        User user = userRepo.findByEmailIgnoreCase(loginRequest.email().trim().toLowerCase()).orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return responseFor(user);
    }

    private AuthDtos.AuthResponse responseFor(User user){
        return new AuthDtos.AuthResponse(
                jwtService.generate(user.getEmail()),
                new AuthDtos.UserResponse(
                        user.getId(), user.getEmail(), user.getDisplayName(), user.getDailyGoal()
                )
        );
    }
}
