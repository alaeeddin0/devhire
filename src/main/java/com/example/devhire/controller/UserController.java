package com.example.devhire.controller;

import com.example.devhire.dto.user.CreateUserRequest;
import com.example.devhire.dto.user.UpdateUserRequest;
import com.example.devhire.dto.user.UserResponse;
import com.example.devhire.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse createdUser = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(
                userService.updateCurrentUser(
                        authentication.getName(),
                        request));
    }
    
    
    @PatchMapping("/me/deactivate")
    public ResponseEntity<UserResponse> deactivateCurrentUser(
            Authentication authentication) {
        return ResponseEntity.ok(
                userService.deactivateCurrentUser(
                        authentication.getName()));
    }
}
