package com.example.devhire.repo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devhire.model.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, Long id);
}
