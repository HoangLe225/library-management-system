package com.spring.project.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home"; // Renders home.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Renders login.html
    }
}
