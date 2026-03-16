package com.smarthire.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

/**
 * Job Entity
 * Represents a job posting created by HR.
 * Each job contains required skills used for ATS resume matching.
 */
@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-incremented primary key

    @Column(nullable = false)
    private String title; // Job title e.g. Java Developer

    @Column(columnDefinition = "TEXT")
    private String description; // Detailed job description

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills; // Comma separated skills e.g. java,mysql,spring

    @Column(name = "experience_required")
    private int experienceRequired; // Minimum experience in years

    private int vacancies; // Number of open positions

    private String location; // Job location e.g. Delhi, Remote

    private String jobType;       // Full-Time, Part-Time, Internship etc
    private String salaryRange;   // e.g. 3-5 LPA

    @Column(name = "posted_date")
    private LocalDate postedDate; // Date when job was posted

    @Column(name = "closing_date")
    private LocalDate closingDate; // Last date to apply

    // Many jobs can be posted by one HR user
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hr_id", nullable = false)
    private User hr; // HR who posted this job
}