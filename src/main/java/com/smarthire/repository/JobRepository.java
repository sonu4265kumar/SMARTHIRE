package com.smarthire.repository;

import com.smarthire.model.Job;
import com.smarthire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * JobRepository Interface
 * Handles all database operations related to Job.
 * JpaRepository provides built-in methods like save(), findById(), findAll(),
 * delete()
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Find all jobs posted by a specific HR
    List<Job> findByHr(User hr);

    // Custom search query - search jobs by title, skills or location
    @Query("SELECT j FROM Job j WHERE " +
            "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Job> searchJobs(String keyword);
}