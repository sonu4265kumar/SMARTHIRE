package com.smarthire.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * User Entity
 * Represents both HR and Student accounts in the system.
 * Role field determines the type: HR or STUDENT
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // Auto-incremented primary key

    private String name;                // Full name of the user

    @Column(unique = true, nullable = false)
    private String email;               // Unique email used for login

    @Column(nullable = false)
    private String password;            // BCrypt encrypted password

    private String role;                // User role: HR or STUDENT

    private String phone;               // Contact number

    // ========================
    // HR Specific Fields
    // ========================
    private String company;             // Company name
    private String designation;         // e.g. HR Manager
    private String companyLocation;     // e.g. Delhi, Mumbai
    private String companyWebsite;      // e.g. www.infosys.com

    // ========================
    // Student Specific Fields
    // ========================
    private String college;             // College name
    private String degree;              // e.g. B.Tech, BCA
    private String passingYear;         // e.g. 2025, 2026
    private String skills;              // comma separated e.g. java,python,mysql
}