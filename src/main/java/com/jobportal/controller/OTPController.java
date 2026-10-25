package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.OTPService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/otp")
public class OTPController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private UserService userService;

    @GetMapping("/verify")
    public String verifyOtpForm(Model model, @RequestParam("email") String email) {
        model.addAttribute("email", email);
        return "otp-verification";
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestParam("email") String email, @RequestParam("otp") String inputOtp, Model model) {
        User user = userService.findByEmail(email);
        if (user != null && otpService.isOTPValid(user, inputOtp)) {
            user.setEnabled(true);
            user.setOtp(null);
            userService.save(user);
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Invalid or expired OTP");
        return "otp-verification";
    }

    @PostMapping("/resend")
    public String resendOtp(@RequestParam("email") String email, Model model) {
        if (otpService.canResend(email)) {
            User user = userService.findByEmail(email);
            if (user != null) {
                String newOtp = otpService.generateOTP();
                otpService.setOTP(user, newOtp);
                userService.save(user);
                // emailService.sendOTP(user, newOtp);
                otpService.updateResendCooldown(email);
            }
            model.addAttribute("message", "OTP resent");
        } else {
            model.addAttribute("error", "Wait 30 seconds");
        }
        return "otp-verification";
    }
}

