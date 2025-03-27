package com.example.springbootwebsocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to serve the main HTML page
 */
@Controller
public class HomeController {
    
    /**
     * Return the index page
     */
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}
