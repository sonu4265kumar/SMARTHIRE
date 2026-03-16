package com.smarthire.service;

import com.smarthire.model.Job;
import com.smarthire.model.User;
import com.smarthire.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/**
 * JobService Class
 * Handles all business logic related to Job postings.
 * Called by HRController and StudentController.
 */
@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository; // database operations

    /**
     * Save a new job posting to database
     * Automatically sets posted date to today
     */
    public Job saveJob(Job job) {
        job.setPostedDate(LocalDate.now());
        return jobRepository.save(job);
    }

    /**
     * Get a single job by its ID
     */
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    /**
     * Get all jobs from database
     * Used in student jobs page to show all available jobs
     */
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    /**
     * Get all jobs posted by a specific HR
     * Used in HR dashboard and view jobs page
     */
    public List<Job> getJobsByHR(User hr) {
        return jobRepository.findByHr(hr);
    }

    /**
     * Search jobs by keyword
     * Searches in title, skills and location
     */
    public List<Job> searchJobs(String keyword) {
        return jobRepository.searchJobs(keyword);
    }

    /**
     * Update an existing job
     */
    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }

    /**
     * Delete a job by ID
     */
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}