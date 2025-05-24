package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index.html";
    }

    @GetMapping("/cars")
    public String redirectToCars() {
        return "redirect:/cars.html";
    }

    @GetMapping("/owners")
    public String redirectToOwners() {
        return "redirect:/owners.html";
    }
}