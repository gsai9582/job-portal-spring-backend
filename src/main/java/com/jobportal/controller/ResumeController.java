package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.ResumeParserService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class ResumeController {

    @Autowired
    private ResumeParserService resumeParserService;

    @Autowired
    private UserService userService;

    @GetMapping("/resume/upload")
    public String uploadForm() {
        return "resume-upload";
    }

    @PostMapping("/resume/upload")
    public String uploadResume(@RequestParam("resume") MultipartFile file, Authentication auth, Model model) throws IOException {
        User user = userService.findByEmail(auth.getName());
        String text = resumeParserService.extractText(file.getInputStream());
        Set<String> skills = resumeParserService.extractSkills(text);
        user.setSkills(String.join(",", skills));
        userService.save(user);

        model.addAttribute("skills", skills);
        model.addAttribute("textPreview", text.substring(0, 500) + "...");
        model.addAttribute("resumeScore", "85%"); // Example
        return "resume-result";
    }
}

