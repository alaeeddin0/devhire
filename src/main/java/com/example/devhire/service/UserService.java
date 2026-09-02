package com.example.devhire.service;

import com.example.devhire.dto.user.CreateUserRequest;
import com.example.devhire.dto.user.UpdateUserRequest;
import com.example.devhire.dto.user.UserResponse;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.User;
import com.example.devhire.model.UserRole;
import com.example.devhire.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return toUserResponse(getUserEntityById(id));
    }

    private User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable avec l'id : " + id));

    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Cet email est déjà utilisé.");
        }

        if (request.role() == UserRole.ADMIN) {
            throw new IllegalArgumentException(
                    "Un compte administrateur ne peut pas être créé ici.");
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setRole(request.role());

        String passwordHash = passwordEncoder.encode(request.password());
        user.setPasswordHash(passwordHash);

        User savedUser = userRepository.save(user);

        return toUserResponse(savedUser);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.isActive()
        );
    }

    @Transactional
    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request) {
        User user = getUserEntityById(id);

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new IllegalArgumentException(
                    "Cet email est déjà utilisé par un autre utilisateur.");
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());

        if (request.password() != null) {
            user.setPasswordHash(
                    passwordEncoder.encode(request.password()));
        }

        return toUserResponse(userRepository.save(user));
    }
    
    @Transactional
    public UserResponse deactivateUser(Long id) {
        User user = getUserEntityById(id);

        user.setActive(false);

        return toUserResponse(userRepository.save(user));
    }
}