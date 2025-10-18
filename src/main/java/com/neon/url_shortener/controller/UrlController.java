package com.neon.url_shortener.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neon.url_shortener.model.Url;
import com.neon.url_shortener.model.User;
import com.neon.url_shortener.service.UrlService;
import com.neon.url_shortener.service.UserService;

@RequestMapping("/api")
@RestController
public class UrlController {
    @Autowired
    private UserService userService;
    @Autowired
    private UrlService urlService;
    @PostMapping("/create")
    public ResponseEntity<?> addUrl(@RequestBody Url request, Authentication authentication) {
        urlService.createUrl(request.getOriginalUrl(), authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body("Added Successfully");
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllUrl(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = userOpt.get();

        // ✅ Fetch all URLs created by this user
        List<Url> urls = urlService.getAllUrlsByUser(user);

        return ResponseEntity.ok(urls);
    }
    @PostMapping("/custom")
    public ResponseEntity<?> createCustomUrl(@RequestBody Url request,
                                             Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        try {
            Url url = urlService.createCustomUrl(
                    request.getOriginalUrl(),
                    request.getShortUrl(),
                    userOpt.get()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(url);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Additional Essential Endpoints

    /**
     * Get specific URL details by ID
     */
    @GetMapping("/urls/{id}")
    public ResponseEntity<?> getUrlById(@PathVariable String id, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = userOpt.get();
        Optional<Url> urlOpt = urlService.getUrlById(id);

        if (urlOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL not found");
        }

        Url url = urlOpt.get();
        if (!url.getCreatedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to access this URL");
        }

        return ResponseEntity.ok(url);
    }

    /**
     * Update URL details
     */
    @PutMapping("/urls/{id}")
    public ResponseEntity<?> updateUrl(@PathVariable String id, @RequestBody Url request, 
                                      Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        try {
            Url updatedUrl = urlService.updateUrl(id, request.getOriginalUrl(), request.getShortUrl(), userOpt.get());
            return ResponseEntity.ok(updatedUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Delete a URL
     */
    @DeleteMapping("/urls/{id}")
    public ResponseEntity<?> deleteUrl(@PathVariable String id, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        try {
            urlService.deleteUrl(id, userOpt.get());
            return ResponseEntity.ok("URL deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }




}
