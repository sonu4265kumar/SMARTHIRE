package com.smarthire.service;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.util.*;

/**
 * ATSService Class
 * ATS = Applicant Tracking System
 * Handles resume parsing and ATS score calculation.
 * Uses Apache Tika to extract text from PDF and DOC files.
 */
@Service
public class ATSService {

    @Value("${file.upload-dir}")
    private String uploadDir; // folder path from application.properties

    /**
     * Parse resume file and extract plain text
     * Supports PDF and DOC/DOCX formats
     */
    public String parseResume(MultipartFile file) throws Exception {
        Tika tika = new Tika();
        tika.setMaxStringLength(100000); // max characters to extract
        return tika.parseToString(file.getInputStream());
    }

    /**
     * Save uploaded resume file to uploads/resumes folder
     * Returns saved filename
     */
    public String saveResumeFile(MultipartFile file, Long studentId) throws Exception {

        // Create unique filename using student id and timestamp
        String filename = "student_" + studentId + "_" +
                System.currentTimeMillis() + "_" +
                file.getOriginalFilename();

        // Create directory if it does not exist
        Path dirPath = Paths.get(uploadDir);
        Files.createDirectories(dirPath);

        // Save file to directory
        Path filePath = dirPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath,
                StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    /**
     * Calculate ATS score by matching resume text with required skills
     * Returns score as percentage e.g. 75.0
     */
    public double calculateScore(String resumeText, String requiredSkills) {

        if (resumeText == null || requiredSkills == null)
            return 0;

        // Split required skills by comma
        String[] skills = requiredSkills.toLowerCase().split(",");
        String resumeLower = resumeText.toLowerCase();

        int matchedCount = 0;

        // Check each skill in resume text
        for (String skill : skills) {
            if (resumeLower.contains(skill.trim())) {
                matchedCount++;
            }
        }

        // Calculate percentage
        double score = ((double) matchedCount / skills.length) * 100;

        // Round to 2 decimal places
        return Math.round(score * 100.0) / 100.0;
    }

    /**
     * Get list of skills found in resume
     */
    public List<String> getMatchedSkills(String resumeText, String requiredSkills) {

        String[] skills = requiredSkills.toLowerCase().split(",");
        String resumeLower = resumeText.toLowerCase();
        List<String> matched = new ArrayList<>();

        for (String skill : skills) {
            if (resumeLower.contains(skill.trim())) {
                matched.add(skill.trim());
            }
        }
        return matched;
    }

    /**
     * Get list of skills missing in resume
     */
    public List<String> getMissingSkills(String resumeText, String requiredSkills) {

        String[] skills = requiredSkills.toLowerCase().split(",");
        String resumeLower = resumeText.toLowerCase();
        List<String> missing = new ArrayList<>();

        for (String skill : skills) {
            if (!resumeLower.contains(skill.trim())) {
                missing.add(skill.trim());
            }
        }
        return missing;
    }

    /**
     * Returns score label based on percentage
     */
    public String getScoreLabel(double score) {
        if (score >= 75)
            return "Excellent Match";
        if (score >= 50)
            return "Good Match";
        if (score >= 25)
            return "Average Match";
        return "Low Match";
    }
}