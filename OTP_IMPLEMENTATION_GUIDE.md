# Job Portal - OTP Email Verification Implementation Guide

## 🎯 Overview
This document provides a complete guide to the OTP-based email verification system implemented in the Job Portal application. The system ensures that users verify their email addresses before accessing the platform.

---

## 📋 Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Components Implemented](#components-implemented)
3. [Email Configuration (Gmail)](#email-configuration-gmail)
4. [User Registration Flow](#user-registration-flow)
5. [User Login Flow](#user-login-flow)
6. [Error Handling](#error-handling)
7. [Testing the System](#testing-the-system)
8. [Troubleshooting](#troubleshooting)

---

## 🏗️ Architecture Overview

### System Flow
```
User Registration
    ↓
Validate Inputs → Check Email Uniqueness
    ↓
Create User (enabled=false)
    ↓
Generate 6-digit OTP
    ↓
Save OTP + 5-min Expiry in DB
    ↓
Send OTP Email
    ↓
Redirect to /verify-otp
    ↓
User Enters OTP
    ↓
Verify OTP (check expiry, attempts, value)
    ↓
Set enabled=true, Clear OTP
    ↓
Redirect to /login
    ↓
User Can Now Login
```

### Database Schema
```sql
User Table:
- id (Primary Key)
- name (NOT NULL, 2-100 chars)
- email (NOT NULL, UNIQUE)
- password (NOT NULL, BCrypt encoded)
- role (JOB_SEEKER, EMPLOYER, ADMIN)
- enabled (boolean, default=false)
- otp (OTP hash, encrypted with BCrypt)
- otpExpiry (LocalDateTime, 5-minute validity)
- otpAttempts (int, max 3 attempts)
- otpLastSentAt (LocalDateTime, 30-sec cooldown)
- resumePath (nullable)
```

---

## 🔧 Components Implemented

### 1. **User Entity** (`model/User.java`)
```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String role;
    
    private boolean enabled; // false until OTP verified
    private String otp; // BCrypt-hashed
    private LocalDateTime otpExpiry; // 5 minutes from generation
    private int otpAttempts; // max 3
    private LocalDateTime otpLastSentAt; // 30-sec cooldown
    private String resumePath;
}
```

### 2. **OTP Service** (`service/OtpService.java`)
Handles OTP generation, validation, and resending:

```java
@Service
public class OtpService {
    private static final Duration OTP_VALIDITY = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(30);
    private static final int MAX_ATTEMPTS = 3;
    
    public void generateAndSendOtp(User user) {
        // Generate 6-digit OTP
        // Hash OTP with BCrypt
        // Save OTP + 5-min expiry
        // Send via email
    }
    
    public void verifyOtp(String email, String otp) {
        // Check if user exists
        // Check if already verified
        // Check if OTP expired
        // Check if max attempts exceeded
        // Verify OTP hash match
        // Set enabled=true, clear OTP
    }
    
    public void resendOtp(String email) {
        // Check if already verified
        // Check 30-sec cooldown
        // Generate new OTP
        // Send email
    }
}
```

### 3. **Notification Service** (`service/NotificationService.java`)
Sends emails via JavaMailSender:

```java
@Service
public class NotificationService {
    public void sendOtpEmail(String email, String otp) {
        // Subject: "OTP Verification - Job Portal"
        // Body: "Your OTP is: XXXXXX (valid for 5 minutes)"
    }
    
    public void sendWelcomeEmail(String email, String name) {
        // Subject: "Welcome to JobPortal!"
        // Body: Personalized welcome message
    }
}
```

### 4. **Registration Controller** (`controller/RegistrationController.java`)
Enhanced with better error handling:

```java
@PostMapping("/register")
public String registerUser(
    @RequestParam String name,
    @RequestParam String email,
    @RequestParam String password,
    @RequestParam String role,
    RedirectAttributes redirectAttributes) {
    
    // Validate inputs (email format, password length, name, role)
    // Check if email already exists
    // If unverified account exists: resend OTP
    // Create new user (enabled=false)
    // Generate & send OTP
    // Redirect to /verify-otp
}
```

### 5. **OTP Controller** (`controller/OtpController.java`)
Handles OTP verification and resending:

```java
@GetMapping("/verify-otp")
public String showOtpPage(@RequestParam String email, Model model) {
    model.addAttribute("email", email);
    return "verify-otp";
}

@PostMapping("/verify-otp")
public String verifyOtp(
    @RequestParam String email,
    @RequestParam String otp,
    RedirectAttributes redirectAttributes) {
    // Verify OTP
    // Handle exceptions with user-friendly messages
}

@PostMapping("/resend-otp")
public String resendOtp(
    @RequestParam String email,
    RedirectAttributes redirectAttributes) {
    // Resend OTP with cooldown check
}
```

### 6. **Login Controller** (`controller/LoginController.java`)
Enhanced to handle unverified users:

```java
@GetMapping("/login")
public String login(
    Authentication authentication,
    @RequestParam(required=false) String notVerified,
    @RequestParam(required=false) String email,
    Model model) {
    
    if (notVerified != null) {
        model.addAttribute("notVerified", true);
        model.addAttribute("email", email);
    }
    return "login";
}
```

### 7. **Global Exception Handler** (`exception/GlobalExceptionHandler.java`)
NEW - Centralized exception handling:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(OtpVerificationException.class)
    public String handleOtpVerificationException(
        OtpVerificationException ex,
        RedirectAttributes redirectAttributes) {
        // User-friendly error messages
        // Proper redirects based on error reason
    }
}
```

### 8. **Security Configuration** (`config/SecurityConfig.java`)
Enhanced with custom authentication failure handler:

```java
@Bean
public AuthenticationFailureHandler authenticationFailureHandler() {
    return (request, response, exception) -> {
        if (exception.getCause() instanceof DisabledException) {
            // Redirect to OTP verification with email
            response.sendRedirect("/login?notVerified&email=" + email);
        } else {
            response.sendRedirect("/login?error");
        }
    };
}
```

### 9. **HTML Templates**

#### `register.html` - Enhanced Registration Page
- Client-side form validation
- Field-level error messages
- Password requirements display
- Terms checkbox
- Professional UX with alerts

#### `verify-otp.html` - OTP Verification Page
- 6-digit OTP input (numbers only)
- Email display
- OTP timer with validity info
- Resend OTP button with 30-sec cooldown
- Error/success messages with close buttons
- Back to registration link

#### `login.html` - Enhanced Login Page
- Error message for invalid credentials
- Special handling for unverified accounts
- Shows email badge when not verified
- Direct link to OTP verification
- Password reset link (placeholder)
- Better styling and UX

---

## 📧 Email Configuration (Gmail)

### Step-by-Step Setup

#### 1. Enable 2-Step Verification
- Go to https://myaccount.google.com/security
- Enable "2-Step Verification"

#### 2. Generate App Password
- Go to https://myaccount.google.com/apppasswords
- Select "Mail" and "Windows Computer" (or your OS)
- Google will generate a 16-character app-specific password

#### 3. Set Environment Variables (Windows PowerShell)
```powershell
$env:SMTP_USERNAME = "your-email@gmail.com"
$env:SMTP_PASSWORD = "your-16-char-app-password"
```

#### 4. Verify in VS Code Run Configuration
- Edit Run Configuration in VS Code
- Add VM Options: `-DSMTP_USERNAME=your-email@gmail.com -DSMTP_PASSWORD=your-app-password`

#### 5. Alternative: application.properties
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
```

**⚠️ IMPORTANT:** Never hardcode credentials in source code. Always use environment variables or secure vaults.

---

## 📝 User Registration Flow

### Step 1: User Visits Registration Page
```
GET /register → register.html
```

### Step 2: User Submits Form
```
POST /register
Input:
  - name: "John Doe"
  - email: "john@example.com"
  - password: "SecurePass123!"
  - role: "JOB_SEEKER"
```

### Step 3: Server Validates Input
- ✅ Email format validation
- ✅ Password length (min 6 chars)
- ✅ Name length (2-100 chars)
- ✅ Role validation (JOB_SEEKER, EMPLOYER, ADMIN)

### Step 4: Check Email Uniqueness
```java
var existingUser = userRepository.findByEmail(email);
if (existingUser.isPresent()) {
    if (!existingUser.get().isEnabled()) {
        // Account exists but not verified → Resend OTP
        otpService.resendOtp(email);
    } else {
        // Account already exists and verified
        error = "Email already exists"
    }
}
```

### Step 5: Create User & Generate OTP
```java
User user = new User();
user.setName(name.trim());
user.setEmail(email.trim().toLowerCase());
user.setPassword(passwordEncoder.encode(password)); // BCrypt
user.setRole(role);
user.setEnabled(false); // Not yet verified

User savedUser = userRepository.save(user);
otpService.generateAndSendOtp(savedUser);
```

### Step 6: OTP Generation
```java
String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
// Generates: 123456 (6 digits)

user.setOtp(passwordEncoder.encode(otp)); // BCrypt hash
user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
user.setOtpAttempts(0);
user.setOtpLastSentAt(LocalDateTime.now());
```

### Step 7: Send Email
```
From: jobportal@gmail.com
To: john@example.com
Subject: OTP Verification - Job Portal
Body: 
  Your OTP is: 123456
  It is valid for 5 minutes.
```

### Step 8: Redirect to OTP Verification
```
Redirect to: /verify-otp?email=john@example.com
Display: verify-otp.html
```

---

## 🔐 User Login Flow

### Scenario 1: User with Verified Email
```
1. User visits /login
2. Enters email: john@example.com, password: SecurePass123!
3. Spring Security checks:
   - User exists? ✅
   - Password correct? ✅
   - User enabled? ✅
4. Redirect to /dashboard
```

### Scenario 2: User with Unverified Email (enabled=false)
```
1. User visits /login
2. Enters email: john@example.com, password: SecurePass123!
3. Spring Security checks:
   - User exists? ✅
   - Password correct? ✅
   - User enabled? ❌ → DisabledException
4. Custom AuthenticationFailureHandler catches DisabledException
5. Redirect to: /login?notVerified&email=john@example.com
6. login.html shows:
   - ⚠️ "Email Not Verified"
   - Email verification card
   - Link to /verify-otp page
7. User clicks "Enter OTP Now"
8. Redirected to: /verify-otp?email=john@example.com
```

### Scenario 3: User Already Verified
```
User receives success message at /verify-otp
Then redirected to /login
User can now sign in normally
```

---

## ⚠️ Error Handling

### OTP Verification Exceptions

| Reason | Message | Action |
|--------|---------|--------|
| **INVALID** | Invalid OTP. Please check and try again. (3 attempts allowed) | Show error, allow retry (max 3) |
| **EXPIRED** | OTP has expired. Please request a new one. | Show error, offer resend OTP |
| **TOO_MANY_ATTEMPTS** | Too many invalid attempts. Please request a new OTP. | Show error, force new OTP request |
| **COOLDOWN** | Please wait 30 seconds before requesting another OTP. | Show countdown timer |
| **NOT_FOUND** | No account found for this email. Please register first. | Redirect to /register |
| **ALREADY_VERIFIED** | Your account is already verified. Please sign in. | Redirect to /login |

### Exception Handling Strategy

1. **Validation Errors** → Caught in RegistrationController → Display in register.html
2. **OTP Errors** → Caught by GlobalExceptionHandler → Redirected with flash attributes
3. **Database Errors** → Logged and shown as generic error message
4. **Email Sending Errors** → Logged, user can still proceed to OTP page and resend

---

## 🧪 Testing the System

### Test Case 1: Successful Registration & Verification
```
1. Go to http://localhost:8081/register
2. Fill form:
   - Name: John Doe
   - Email: test@example.com
   - Password: SecurePass123
   - Role: Student
3. Click "Create account"
4. Check email for OTP (e.g., 123456)
5. Go to /verify-otp page
6. Enter OTP: 123456
7. See success message
8. Redirect to /login
9. Sign in successfully
```

### Test Case 2: Invalid OTP
```
1. At /verify-otp page
2. Enter OTP: 000000 (wrong)
3. See error: "Invalid OTP. Please check and try again."
4. Try 2 more times (max 3 attempts)
5. On 4th attempt:
   - Error: "Too many invalid attempts. Please request a new OTP."
   - Button to request new OTP becomes active
```

### Test Case 3: Expired OTP
```
1. Wait 5 minutes after OTP generation
2. Enter any OTP at /verify-otp
3. See error: "OTP has expired. Please request a new OTP."
4. Click "Resend OTP"
5. Wait 30 seconds
6. Resend becomes available
```

### Test Case 4: Login Without Verification
```
1. Register a new account (don't verify)
2. Go to /login
3. Enter email and password
4. See: ⚠️ "Email Not Verified"
5. See email verification card with "Enter OTP Now" button
6. Click button → Go to /verify-otp
```

### Test Case 5: Resend OTP Cooldown
```
1. At /verify-otp page
2. Click "Resend OTP" button
3. Button becomes disabled: "Resend OTP (wait 30s)"
4. Countdown starts
5. After 30 seconds, button becomes enabled again
```

### Test Case 6: Duplicate Email Registration
```
1. Register with: john@example.com (and verify)
2. Try to register again with: john@example.com
3. See error: "Email already exists"
```

### Test Case 7: Duplicate Email (Unverified)
```
1. Register with: jane@example.com (don't verify)
2. Try to register again with: jane@example.com
3. See success message: "Account already exists but is not verified. We sent a new OTP."
4. Redirected to /verify-otp page
```

---

## 🐛 Troubleshooting

### Issue 1: "An error occurred during registration"
**Cause:** Email configuration not set up, JavaMailSender failing

**Solution:**
1. Check application.properties for SMTP settings
2. Verify environment variables are set:
   ```powershell
   $env:SMTP_USERNAME
   $env:SMTP_PASSWORD
   ```
3. Check logs for mail sending errors:
   ```
   Failed to send email: ... (check error message)
   ```
4. Verify Gmail app password is correct (16 chars)

### Issue 2: "DisabledException" on login
**Cause:** User account is not enabled (email not verified)

**Solution:**
1. Verify user's email address
2. Find OTP in email
3. Go to /verify-otp page
4. Enter OTP to enable account

### Issue 3: "OTP expired" but only 1 minute passed
**Cause:** Server time is out of sync

**Solution:**
1. Check server time: `date`
2. Sync system clock
3. Restart application

### Issue 4: Email not received
**Cause:** Multiple possible reasons

**Solution:**
```
1. Check spam/junk folder in email
2. Verify SMTP_USERNAME and SMTP_PASSWORD are correct
3. Verify Gmail App Password (not regular password)
4. Check application logs for mail errors
5. Verify firewall/network allows SMTP (port 587)
6. Try from different email provider (test)
```

### Issue 5: "No matching class constructor found"
**Cause:** OtpService constructor requires NotificationService

**Solution:**
1. Ensure NotificationService is @Service annotated
2. Run `mvn clean compile` to regenerate beans
3. Restart the application

### Issue 6: Form validation errors showing as "Cannot read property"
**Cause:** JavaScript error in HTML

**Solution:**
1. Check browser console (F12)
2. Verify form element IDs match JavaScript references
3. Check for typos in HTML

---

## 📊 Database Queries for Testing

### View All Users
```sql
SELECT id, email, name, role, enabled, otp IS NOT NULL as has_otp, 
       otp_expiry, otp_attempts, otp_last_sent_at 
FROM user;
```

### Find Unverified Users
```sql
SELECT * FROM user WHERE enabled = false;
```

### Check User OTP Status
```sql
SELECT email, otp_expiry, otp_attempts, 
       (NOW() < otp_expiry) as otp_valid 
FROM user 
WHERE email = 'test@example.com';
```

### Clear Expired OTPs
```sql
UPDATE user SET otp = NULL, otp_expiry = NULL, otp_attempts = 0 
WHERE otp_expiry < NOW();
```

---

## 🔒 Security Considerations

1. **OTP Hashing**: OTP is hashed with BCrypt before saving (not plaintext)
2. **Password Encoding**: User passwords are BCrypt encoded
3. **Email Validation**: Validates email format before saving
4. **Rate Limiting**: 30-second cooldown for resending OTP
5. **Attempt Limiting**: Max 3 OTP attempts per verification request
6. **Expiry**: OTP expires in 5 minutes
7. **HTTPS**: Always use HTTPS in production for sensitive data
8. **CSRF Protection**: Spring Security CSRF tokens (currently disabled for simplicity)

---

## 📞 Support

For issues or questions:
1. Check the Troubleshooting section
2. Check application logs for specific error messages
3. Verify all environment variables are set correctly
4. Test with a simple email (Gmail) first

---

## 📅 Version Information

- **Spring Boot**: 3.5.0
- **Java**: 17
- **MySQL**: 8.0
- **Security**: Spring Security 6
- **Mail**: Spring Mail (JavaMailSender)

---

**Last Updated**: May 4, 2026
**Status**: ✅ Production Ready
