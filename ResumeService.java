package com.tracker.jobtracker.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeService {

    private final Tika tika = new Tika();

    // Map defining role-specific target keywords for accurate ATS scoring
    private final Map<String, List<String>> roleKeywordMap = new HashMap<>();

    public ResumeService() {
        // Python Developer required skills
        roleKeywordMap.put("python developer", Arrays.asList(
            "python", "django", "flask", "fastapi", "pandas", "numpy", 
            "pytest", "rest api", "postgresql", "docker", "git", "data science"
        ));

        // Java Developer required skills
        roleKeywordMap.put("java developer", Arrays.asList(
            "java", "spring boot", "spring", "hibernate", "maven", "microservices", 
            "jpa", "rest api", "sql", "junit", "docker", "git"
        ));

        // Frontend / Full Stack skills
        roleKeywordMap.put("frontend developer", Arrays.asList(
            "javascript", "react", "html", "css", "node", "typescript", 
            "tailwind", "redux", "git", "rest api"
        ));

        // Default fallback skills for generic software roles
        roleKeywordMap.put("software engineer", Arrays.asList(
            "java", "python", "javascript", "sql", "git", "docker", 
            "rest api", "data structures", "algorithms", "agile"
        ));
    }

    public String extractText(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            return tika.parseToString(inputStream);
        }
    }

    public int calculateMatchScore(String resumeText, String targetRole) {
        if (resumeText == null || resumeText.trim().isEmpty() || targetRole == null) {
            return 0;
        }

        String lowerResume = resumeText.toLowerCase();
        String lowerRole = targetRole.toLowerCase().trim();

        // Determine matching skill keywords for the given role
        List<String> requiredKeywords = null;
        for (Map.Entry<String, List<String>> entry : roleKeywordMap.entrySet()) {
            if (lowerRole.contains(entry.getKey()) || entry.getKey().contains(lowerRole)) {
                requiredKeywords = entry.getValue();
                break;
            }
        }

        // If no specific role match is found, fallback to generic software developer keywords
        if (requiredKeywords == null) {
            requiredKeywords = roleKeywordMap.get("software engineer");
        }

        int matchedCount = 0;
        for (String keyword : requiredKeywords) {
            if (lowerResume.contains(keyword)) {
                matchedCount++;
            }
        }

        // Calculate accurate percentage score based on matching role keywords
        return (int) Math.round(((double) matchedCount / requiredKeywords.size()) * 100);
    }
}