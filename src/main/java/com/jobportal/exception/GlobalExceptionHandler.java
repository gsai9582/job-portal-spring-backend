package com.jobportal.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global exception handler for handling all application exceptions
 * Provides user-friendly error messages and proper error handling
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle OTP verification exceptions
     * Provides specific error messages based on the reason
     */
    @ExceptionHandler(OtpVerificationException.class)
    public String handleOtpVerificationException(
            OtpVerificationException ex,
            RedirectAttributes redirectAttributes) {
        
        String errorMessage = getOtpErrorMessage(ex.getReason(), ex.getMessage());
        redirectAttributes.addFlashAttribute("error", errorMessage);
        
        // Redirect based on the reason
        switch (ex.getReason()) {
            case ALREADY_VERIFIED:
                return "redirect:/login";
            case NOT_FOUND:
                return "redirect:/register";
            default:
                // Extract email from exception or use a default
                return "redirect:/verify-otp?email=user@example.com";
        }
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(
            Exception ex,
            RedirectAttributes redirectAttributes) {
        
        redirectAttributes.addFlashAttribute("error", 
            "An unexpected error occurred. Please try again.");
        return "redirect:/register";
    }

    /**
     * Get user-friendly error message based on OTP reason
     */
    private String getOtpErrorMessage(OtpVerificationException.Reason reason, String defaultMessage) {
        switch (reason) {
            case INVALID:
                return "Invalid OTP. Please check and try again. (3 attempts allowed)";
            case EXPIRED:
                return "OTP has expired. Please request a new one.";
            case COOLDOWN:
                return "Please wait 30 seconds before requesting another OTP.";
            case TOO_MANY_ATTEMPTS:
                return "Too many invalid attempts. Please request a new OTP.";
            case NOT_FOUND:
                return "No account found for this email. Please register first.";
            case ALREADY_VERIFIED:
                return "Your account is already verified. Please sign in.";
            default:
                return defaultMessage;
        }
    }
}
