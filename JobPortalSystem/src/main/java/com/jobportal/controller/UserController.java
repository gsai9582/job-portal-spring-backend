package com.jobportal.controller;

import com.jobportal.dto.UserDTO;
import com.jobportal.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllUsers(Model model) {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", users.size());
        return "users";
    }

    // REST API endpoint (optional, for AJAX/future use)
    @RestController
    @RequestMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    class ApiController {
        @GetMapping
        public List<UserDTO> getAllUsersApi() {
            return userService.getAllUsers();
        }
    }
}
