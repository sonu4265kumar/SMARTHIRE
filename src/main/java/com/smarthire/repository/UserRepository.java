package com.smarthire.repository;

import com.smarthire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * UserRepository Interface
 * Handles all database operations related to User.
 * JpaRepository provides built-in methods like save(), findById(), findAll(),
 * delete()
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email - used during login
    Optional<User> findByEmail(String email);

    // Find all users by role - get all HR or all Students
    List<User> findByRole(String role);

    // Check if email already exists - used during registration
    boolean existsByEmail(String email);
}