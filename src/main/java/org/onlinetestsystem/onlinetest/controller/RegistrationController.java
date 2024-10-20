package org.onlinetestsystem.onlinetest.controller;

import org.onlinetestsystem.onlinetest.entity.Users;
import org.onlinetestsystem.onlinetest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class RegistrationController {

    @Autowired
    private UserService userService;

    // Show registration form
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new Users());
        return "auth/register";
    }

    // Handle form submission
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") Users user, @RequestParam String passwordConfirm, Model model) {
        if (!user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("error", "Passwords do not match!");
            return "auth/register";
        }

        // Save user (you may need to encode the password)
        try {
            userService.registerUser(user);  // Assuming `registerUser` handles user registration logic
            return "redirect:/auth/login";  // Redirect to login after successful registration
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    // Show login form
//    @GetMapping("/login")
//    public String showLoginForm() {
//        return "auth/login";
//    }
}
