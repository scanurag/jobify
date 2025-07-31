package com.org.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String homePage(Model model, Principal principal) {
      
        if (principal != null) {
            model.addAttribute("email", principal.getName());
        } else {
            model.addAttribute("email", "Guest");
        }

        return "home"; 
    }
}
