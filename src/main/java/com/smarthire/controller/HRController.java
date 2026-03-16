package com.smarthire.controller;

import com.smarthire.model.Application;
import com.smarthire.model.Job;
import com.smarthire.model.User;
import com.smarthire.repository.ApplicationRepository;
import com.smarthire.service.JobService;
import com.smarthire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

/**
 * HRController Class
 * Handles all HR related pages and actions.
 * Accessible only by users with role HR.
 */
@Controller
@RequestMapping("/hr")
public class HRController {

    @Autowired
    private JobService jobService; // job posting logic

    @Autowired
    private UserService userService; // get logged in HR details

    @Autowired
    private ApplicationRepository applicationRepo; // get applications for jobs

    /**
     * HR Dashboard
     * Shows total jobs posted and recent job list
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {

        // Get currently logged in HR
        User hr = userService.findByEmail(auth.getName());

        // Get all jobs posted by this HR
        List<Job> jobs = jobService.getJobsByHR(hr);

        model.addAttribute("hr", hr);
        model.addAttribute("totalJobs", jobs.size());
        model.addAttribute("jobs", jobs);

        return "hr/dashboard";
    }

    /**
     * Show Post Job form page
     */
    @GetMapping("/post-job")
    public String postJobPage(Model model) {
        model.addAttribute("job", new Job());
        return "hr/post-job";
    }

    /**
     * Handle Post Job form submission
     * Saves new job to database
     */
    @PostMapping("/post-job")
    public String postJob(@ModelAttribute Job job,
            Authentication auth,
            RedirectAttributes ra) {

        // Get currently logged in HR
        User hr = userService.findByEmail(auth.getName());

        // Set HR as owner of this job
        job.setHr(hr);

        // Save job to database
        jobService.saveJob(job);

        ra.addFlashAttribute("success", "Job posted successfully!");
        return "redirect:/hr/view-jobs";
    }

    /**
     * Show all jobs posted by logged in HR
     */
    @GetMapping("/view-jobs")
    public String viewJobs(Model model, Authentication auth) {

        User hr = userService.findByEmail(auth.getName());
        List<Job> jobs = jobService.getJobsByHR(hr);

        model.addAttribute("jobs", jobs);

        return "hr/view-jobs";
    }

    /**
     * Show ranked candidates for a specific job
     * Candidates are sorted by ATS score highest first
     */
    @GetMapping("/ranked-candidates/{jobId}")
    public String rankedCandidates(@PathVariable Long jobId,
            Model model) {

        // Get job details
        Job job = jobService.getJobById(jobId);

        // Get all applications sorted by ATS score
        List<Application> applications = applicationRepo.findByJobOrderByAtsScoreDesc(job);

        model.addAttribute("job", job);
        model.addAttribute("applications", applications);

        return "hr/ranked-candidates";
    }

    /**
     * Delete a job posting
     */
    @GetMapping("/delete-job/{jobId}")
    public String deleteJob(@PathVariable Long jobId,
            RedirectAttributes ra) {

        jobService.deleteJob(jobId);

        ra.addFlashAttribute("success", "Job deleted successfully!");
        return "redirect:/hr/view-jobs";
    }
}