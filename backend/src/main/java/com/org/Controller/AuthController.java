package com.org.Controller;
import com.org.Entity.User;
import com.org.Service.JwtService;
import com.org.Service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login/{type}")
    public ResponseEntity<?> login(@PathVariable String type, @RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = userService.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword()) || !user.getRole().equalsIgnoreCase(type)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials"));
        }

        String token = jwtService.generateToken(email, type);
        return ResponseEntity.ok(Map.of("token", token));
    }

  
    @PostMapping("/signup/{type}")
    public ResponseEntity<?> signup(@PathVariable String type, @RequestBody User user) {
        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
        }

        user = userService.registerUser(user, type.toUpperCase());
        return ResponseEntity.ok(Map.of("message", "Signup successful"));
    }
}
