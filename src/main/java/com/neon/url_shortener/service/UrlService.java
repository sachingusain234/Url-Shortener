package com.neon.url_shortener.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.neon.url_shortener.model.Url;
import com.neon.url_shortener.model.User;
import com.neon.url_shortener.repository.UrlRepository;
import com.neon.url_shortener.repository.UserRepository;

@Service
public class UrlService {
    @Autowired
    private UrlRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    public ResponseEntity<?> createUrl(String originalUrl, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> useropt = userService.getUserByUsername(username);
        if(useropt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        User user = useropt.get();
        Optional<Url> existing = repository.findByOriginalUrl(originalUrl);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Short URL already in use");
        }
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(UUID.randomUUID().toString().substring(0,8));
        url.setCreatedAt(LocalDateTime.now());
        url.setCreatedBy(user);
        Url savedUrl = repository.save(url);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUrl);
    }

    public List<Url> getAllUrls(User user) {
        return repository.findAll();
    }
    public void deleteAllUrls() {
        repository.deleteAll();
    }
    public Url getOriginalUrl(String shortUrl) {
        return repository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));
    }
    public Url createCustomUrl(String originalUrl, String customShortUrl, User user) {
        // Check if custom short URL already exists
        Optional<Url> existing = repository.findByShortUrl(customShortUrl);
        Optional<Url> existing1 = repository.findByOriginalUrl(originalUrl);
        if (existing.isPresent() || existing1.isPresent()) {
            throw new IllegalArgumentException(" URL already in use");
        }

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(customShortUrl);
        url.setCreatedAt(LocalDateTime.now());
        url.setCreatedBy(user);

        return repository.save(url);
    }

    // New methods for additional endpoints
    public Optional<Url> getUrlById(String id) {
        return repository.findById(id);
    }

    public List<Url> getAllUrlsByUser(User user) {
        return repository.findAll().stream()
                .filter(url -> url.getCreatedBy() != null && url.getCreatedBy().getId().equals(user.getId()))
                .toList();
    }

    public void deleteUrl(String id, User user) {
        Optional<Url> urlOpt = repository.findById(id);
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            if (url.getCreatedBy() != null && url.getCreatedBy().getId().equals(user.getId())) {
                repository.deleteById(id);
            } else {
                throw new IllegalArgumentException("You don't have permission to delete this URL");
            }
        } else {
            throw new IllegalArgumentException("URL not found");
        }
    }

    public Url updateUrl(String id, String originalUrl, String shortUrl, User user) {
        Optional<Url> urlOpt = repository.findById(id);
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            if (url.getCreatedBy() != null && url.getCreatedBy().getId().equals(user.getId())) {
                if (originalUrl != null) url.setOriginalUrl(originalUrl);
                if (shortUrl != null) {
                    // Check if new short URL is already taken
                    Optional<Url> existing = repository.findByShortUrl(shortUrl);
                    if (existing.isPresent() && !existing.get().getId().equals(id)) {
                        throw new IllegalArgumentException("Short URL already in use");
                    }
                    url.setShortUrl(shortUrl);
                }
                return repository.save(url);
            } else {
                throw new IllegalArgumentException("You don't have permission to update this URL");
            }
        } else {
            throw new IllegalArgumentException("URL not found");
        }
    }

    public Url redirectToOriginal(String shortUrl) {
        Optional<Url> urlOpt = repository.findByShortUrl(shortUrl);
        if (urlOpt.isPresent()) {
            return urlOpt.get();
        } else {
            throw new IllegalArgumentException("Short URL not found");
        }
    }


}

