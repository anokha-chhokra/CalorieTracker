package org.belex.backend.controller;

import jakarta.validation.Valid;
import org.belex.backend.dto.AuthDtos;
import org.belex.backend.model.User;
import org.belex.backend.repo.UserRepo;
import org.belex.backend.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserService userService;
    private final UserRepo userRepo;

    public ProfileController(UserService userService, UserRepo userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    @PutMapping
    public AuthDtos.UserResponse update(@AuthenticationPrincipal UserDetails principal, @Valid @RequestBody AuthDtos.ProfileUpdateRequest request){
        User user = userService.getFromPrincipal(principal);
        user.setDisplayName(request.displayName().trim());
        user.setDailyGoal(request.dailyGoal());
        userRepo.save(user);

        return new AuthDtos.UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getDailyGoal()
        );
    }
}
