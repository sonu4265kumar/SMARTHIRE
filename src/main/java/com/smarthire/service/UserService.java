package com.smarthire.service;

import com.smarthire.model.User;
import com.smarthire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * UserService Class
 * Handles all business logic related to User.
 * Implements UserDetailsService for Spring Security login.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // database operations

    @Autowired
    private PasswordEncoder passwordEncoder; // password encryption

    /**
     * Register a new user (HR or Student)
     * Encrypts password before saving to database
     */
    public User registerUser(User user) {

        // Check if email already registered
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered: " + user.getEmail());
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user to database
        return userRepository.save(user);
    }

    /**
     * Find user by email address
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Find user by ID
     */
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Get all students from database
     */
    public List<User> getAllStudents() {
        return userRepository.findByRole("STUDENT");
    }

    /**
     * This method is called by Spring Security during login.
     * Loads user details by email and returns roles.
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Return Spring Security user with role
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}