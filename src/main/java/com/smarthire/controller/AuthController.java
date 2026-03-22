package com.smarthire.controller;

import com.smarthire.model.User;
import com.smarthire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AuthController Class
 * Handles login, register, home page and dashboard redirect.
 * Routes: /, /login, /register, /dashboard
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService; // handles user registration and login

    /**
     * Show home page
     * GET /
     */
    @GetMapping("/")
    public String home() {
        return "index"; // templates/index.html
    }

    /**
     * Show login page
     * GET /login
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Invalid email or password.");
        }
        if (logout != null) {
            model.addAttribute("logout", "Logged out successfully.");
        }

        return "auth/login"; // templates/auth/login.html
    }

    /**
     * Show register page
     * GET /register
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register"; // templates/auth/register.html
    }

    /**
     * Handle register form submission
     * POST /register
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
            @RequestParam String role,
            RedirectAttributes ra) {
        try {
            // Set role from form (HR or STUDENT)
            user.setRole(role.toUpperCase());

            // Save user to database
            userService.registerUser(user);

            ra.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Redirect user to correct dashboard based on role
     * GET /dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {

        String roles = auth.getAuthorities().toString();

        // HR goes to HR dashboard
        if (roles.contains("ROLE_HR")) {
            return "redirect:/hr/dashboard";
        }

        // Student goes to Student dashboard
        return "redirect:/student/dashboard";
    }
}