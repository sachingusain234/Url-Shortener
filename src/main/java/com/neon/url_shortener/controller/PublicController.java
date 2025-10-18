package com.neon.url_shortener.controller;

import com.neon.url_shortener.model.User;
import com.neon.url_shortener.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/public")
@RestController
public class PublicController {
    @Autowired
    private UserService userService;
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            String token = userService.register(user.getUsername(), user.getPassword(), user.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409 Conflict
                    .body(Map.of("error", "Username already exists"));
        }
    }


    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String token = userService.login(user.getUsername(), user.getPassword());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
