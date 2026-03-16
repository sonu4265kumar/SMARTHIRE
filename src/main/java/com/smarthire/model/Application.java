package com.smarthire.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

/**
 * Application Entity
 * Represents a job application submitted by a Student.
 * Stores the resume file path, extracted text, and ATS score.
 */
@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-incremented primary key

    // Many applications can belong to one student
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student; // Student who applied

    // Many applications can belong to one job
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job; // Job that was applied for

    @Column(name = "resume_path")
    private String resumePath; // Saved resume file name

    @Column(name = "extracted_text", columnDefinition = "LONGTEXT")
    private String extractedText; // Text extracted from resume by Apache Tika

    @Column(name = "ats_score")
    private double atsScore; // Final ATS matching score in percentage

    @Column(name = "matched_skills", columnDefinition = "TEXT")
    private String matchedSkills; // Skills found in resume

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills; // Skills not found in resume

    // Application status flow: APPLIED -> SHORTLISTED -> SELECTED / REJECTED
    private String status; // Current status of application

    @Column(name = "applied_date")
    private LocalDate appliedDate; // Date when student applied
}