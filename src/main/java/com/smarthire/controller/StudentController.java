package com.smarthire.controller;

import com.smarthire.model.Application;
import com.smarthire.model.Job;
import com.smarthire.model.User;
import com.smarthire.repository.ApplicationRepository;
import com.smarthire.service.ATSService;
import com.smarthire.service.JobService;
import com.smarthire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;

/**
 * StudentController Class
 * Handles all Student related pages and actions.
 * Accessible only by users with role STUDENT.
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private JobService jobService; // get all jobs

    @Autowired
    private UserService userService; // get logged in student details

    @Autowired
    private ApplicationRepository applicationRepo; // save and get applications

    @Autowired
    private ATSService atsService; // resume parsing and scoring

    /**
     * Student Dashboard
     * Shows total applications and application list
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {

        User student = userService.findByEmail(auth.getName());
        List<Application> applications =
            applicationRepo.findByStudent(student);

        // Count shortlisted applications
        long totalShortlisted = applications.stream()
            .filter(a -> a.getStatus().equals("SHORTLISTED"))
            .count();

        // Get best ATS score
        double bestScore = applications.stream()
            .mapToDouble(Application::getAtsScore)
            .max()
            .orElse(0.0);

        // Round best score
        bestScore = Math.round(bestScore * 100.0) / 100.0;

        model.addAttribute("student", student);
        model.addAttribute("totalApplied", applications.size());
        model.addAttribute("totalShortlisted", totalShortlisted);
        model.addAttribute("bestScore", bestScore);
        model.addAttribute("applications", applications);

        return "student/dashboard";
    }
  
    /**
     * Show all available jobs
     * Also handles search by keyword
     */
    @GetMapping("/jobs")
    public String jobs(Model model,
            @RequestParam(required = false) String search) {

        List<Job> jobs;

        if (search != null && !search.isEmpty()) {
            // Search jobs by keyword
            jobs = jobService.searchJobs(search);
        } else {
            // Get all jobs
            jobs = jobService.getAllJobs();
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("search", search);

        return "student/jobs";
    }

    /**
     * Show apply job page for a specific job
     */
    @GetMapping("/apply/{jobId}")
    public String applyPage(@PathVariable Long jobId,
            Model model,
            Authentication auth) {

        User student = userService.findByEmail(auth.getName());
        Job job = jobService.getJobById(jobId);

        // Check if student already applied for this job
        boolean alreadyApplied = applicationRepo.existsByStudentAndJob(student, job);

        model.addAttribute("job", job);
        model.addAttribute("alreadyApplied", alreadyApplied);

        return "student/apply-job";
    }

    /**
     * Handle resume upload and job application
     * Parses resume, calculates ATS score and saves application
     */
    @PostMapping("/apply/{jobId}")
    public String applyJob(@PathVariable Long jobId,
            @RequestParam("resume") MultipartFile resume,
            Authentication auth,
            RedirectAttributes ra) {
        try {
            User student = userService.findByEmail(auth.getName());
            Job job = jobService.getJobById(jobId);

            // Check duplicate application
            if (applicationRepo.existsByStudentAndJob(student, job)) {
                ra.addFlashAttribute("error", "You have already applied for this job.");
                return "redirect:/student/jobs";
            }

            // Parse resume and extract text
            String resumeText = atsService.parseResume(resume);

            // Save resume file to uploads folder
            String filename = atsService.saveResumeFile(resume, student.getId());

            // Calculate ATS score
            double score = atsService.calculateScore(resumeText, job.getRequiredSkills());

            // Get matched and missing skills
            String matched = String.join(",",
                    atsService.getMatchedSkills(resumeText, job.getRequiredSkills()));
            String missing = String.join(",",
                    atsService.getMissingSkills(resumeText, job.getRequiredSkills()));

            // Create and save application
            Application application = new Application();
            application.setStudent(student);
            application.setJob(job);
            application.setResumePath(filename);
            application.setExtractedText(resumeText);
            application.setAtsScore(score);
            application.setMatchedSkills(matched);
            application.setMissingSkills(missing);
            application.setStatus("APPLIED");
            application.setAppliedDate(LocalDate.now());

            applicationRepo.save(application);

            ra.addFlashAttribute("success",
                    "Application submitted! Your ATS Score: " + score + "%");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Something went wrong: " + e.getMessage());
        }

        return "redirect:/student/applied-jobs";
    }

    /**
     * Show all jobs applied by logged in student
     */
    @GetMapping("/applied-jobs")
    public String appliedJobs(Model model, Authentication auth) {

        User student = userService.findByEmail(auth.getName());
        List<Application> applications = applicationRepo.findByStudent(student);

        model.addAttribute("applications", applications);

        return "student/applied-jobs";
    }

    /**
     * Show ATS resume screening page
     * Student can check resume score without applying
     */
    @GetMapping("/ats-check")
    public String atsCheckPage() {
        return "student/ats-result";
    }

    /**
     * Handle ATS check form submission
     * Parses resume and calculates score against entered skills
     */
    @PostMapping("/ats-check")
    public String atsCheck(@RequestParam("resume") MultipartFile resume,
            @RequestParam("skills") String skills,
            Model model) {
        try {
            // Parse resume text
            String resumeText = atsService.parseResume(resume);

            // Calculate score
            double score = atsService.calculateScore(resumeText, skills);

            // Get matched and missing skills
            List<String> matched = atsService.getMatchedSkills(resumeText, skills);
            List<String> missing = atsService.getMissingSkills(resumeText, skills);

            // Get score label
            String label = atsService.getScoreLabel(score);

            model.addAttribute("score", score);
            model.addAttribute("matched", matched);
            model.addAttribute("missing", missing);
            model.addAttribute("label", label);

        } catch (Exception e) {
            model.addAttribute("error", "Could not process resume: " + e.getMessage());
        }

        return "student/ats-result";
    }
}