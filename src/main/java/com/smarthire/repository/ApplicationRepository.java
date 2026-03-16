package com.smarthire.repository;

import com.smarthire.model.Application;
import com.smarthire.model.Job;
import com.smarthire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * ApplicationRepository Interface
 * Handles all database operations related to Job Applications.
 * JpaRepository provides built-in methods like save(), findById(), findAll(),
 * delete()
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Count total applications for a specific job
    long countByJob(Job job);

    // Find all applications submitted by a specific student
    List<Application> findByStudent(User student);

    // Find all applications for a specific job - sorted by ATS score highest first
    List<Application> findByJobOrderByAtsScoreDesc(Job job);

    // Find a specific application by student and job - used for duplicate check
    Optional<Application> findByStudentAndJob(User student, Job job);

    // Check if student has already applied for a job
    boolean existsByStudentAndJob(User student, Job job);
}