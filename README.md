# AI-Enhanced Job Portal Backend (Spring Boot 3 + MySQL)

A production-grade, enterprise backend system for full-lifecycle recruitment and job application management. Built with **Spring Boot 3.2.5**, **Spring Security 6**, **Spring Data JPA**, **MySQL**, and **Apache Tika** for intelligent resume text extraction.

---

## 🏗️ Architecture & Key Features

### 1. Role-Based Access Control (RBAC) & Security
- Secure session authentication and role authorizations: `ROLE_JOB_SEEKER`, `ROLE_EMPLOYER`, `ROLE_ADMIN`.
- BCrypt password encryption with salted hashing.
- CSRF protection and customized access denial handlers.

### 2. Multi-Factor OTP Email Authentication
- Time-based One-Time Password (OTP) verification for secure user registration and password resets.
- SMTP JavaMail integration with dynamic HTML email templating.
- Complete specification in [**`OTP_IMPLEMENTATION_GUIDE.md`**](./OTP_IMPLEMENTATION_GUIDE.md).

### 3. Automated Resume Parsing Engine (Apache Tika)
- Integrated Apache Tika (`tika-core` & `tika-parsers-standard-package 2.9.2`).
- Automatically extracts text, contact info, and skill tags from uploaded PDF and DOCX applicant resumes.

### 4. RESTful API & Web MVC Layer
- Complete REST API suite for job listings, applicant submissions, employer dashboards, and administrative audits.
- Full endpoint specifications detailed in [**`API_DOCUMENTATION.md`**](./API_DOCUMENTATION.md).

---

## 🛠️ Technology Stack

- **Core Framework:** Spring Boot 3.2.5 (Java 17+)
- **Security:** Spring Security 6 (BCrypt, Session Management)
- **Persistence & ORM:** Spring Data JPA, Hibernate ORM
- **Database:** MySQL 8+ / H2 In-Memory
- **Content Parsing:** Apache Tika 2.9.2
- **Notification:** Spring Boot Mail Starter (JavaMailSender)
- **Templating:** Thymeleaf & Thymeleaf Extras Spring Security 6
- **Build Tool:** Apache Maven

---

## 📂 Project Structure

```
job-portal-spring-backend/
├── docs/
│   └── presentation/          # Project slides & milestones
├── src/
│   ├── main/
│   │   ├── java/com/jobportal/
│   │   │   ├── config/        # Security & Application configurations
│   │   │   ├── controller/    # Web & REST Controllers
│   │   │   ├── data/          # Database seeding & initializers
│   │   │   ├── dto/           # Data Transfer Objects
│   │   │   ├── exception/     # Global exception handling
│   │   │   ├── model/         # JPA Domain Entities
│   │   │   ├── repo/          # Spring Data JPA Repositories
│   │   │   └── service/       # Business Logic & OTP/Mail Services
│   │   └── resources/
│   │       ├── templates/     # Thymeleaf UI views
│   │       └── application.properties
│   └── test/                  # JUnit 5 & Mockito test suites
├── API_DOCUMENTATION.md       # Full REST API Contract
├── OTP_IMPLEMENTATION_GUIDE.md# Security & OTP Verification Guide
└── pom.xml
```

---

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- Apache Maven 3.8+
- MySQL Server 8+

### Database Setup
1. Create the MySQL database:
   ```sql
   CREATE DATABASE jobportal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Configure credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/jobportal?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

### Building & Running
1. Build the application:
   ```bash
   mvn clean package -DskipTests
   ```
2. Start the Spring Boot server:
   ```bash
   mvn spring-boot:run
   ```
3. Open your browser:
   ```
   http://localhost:8080
   ```

---

## 📄 License
MIT License.
