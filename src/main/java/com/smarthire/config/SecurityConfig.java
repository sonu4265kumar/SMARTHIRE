package com.smarthire.config;

import com.smarthire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig Class
 * Configures Spring Security for the application.
 * Defines login, logout, role based access and password encryption.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService; // used for loading user during login

    /**
     * Defines which routes are accessible by whom
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // These pages are open for everyone
                        .requestMatchers("/login", "/register",
                                "/css/**", "/js/**")
                        .permitAll()
                        // Only HR can access /hr/ pages
                        .requestMatchers("/hr/**").hasRole("HR")
                        // Only Student can access /student/ pages
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        // All other pages require login
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login") // custom login page
                        .loginProcessingUrl("/login") // form action url
                        .defaultSuccessUrl("/dashboard", true) // redirect after login
                        .failureUrl("/login?error=true") // redirect on wrong password
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout") // logout url
                        .logoutSuccessUrl("/login?logout=true") // redirect after logout
                        .permitAll());

        return http.build();
    }

    /**
     * Password encoder using BCrypt algorithm
     * Used to encrypt password during registration
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager
     * Connects UserService and PasswordEncoder for login verification
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
}