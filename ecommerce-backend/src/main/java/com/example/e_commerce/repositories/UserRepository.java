package com.example.e_commerce.repositories;

import com.example.e_commerce.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);


    // You can define custom query methods here if needed later
}
