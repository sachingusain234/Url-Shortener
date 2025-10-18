package com.neon.url_shortener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.neon.url_shortener.model.Url;
import com.neon.url_shortener.service.UrlService;

@RestController
public class RedirectController {

    @Autowired
    private UrlService urlService;

    /**
     * Public endpoint for URL redirection - no authentication required
     * This is the main endpoint users will hit to access shortened URLs
     */
    @GetMapping("/public/{shortUrl}")
    public ResponseEntity<?> redirectToOriginal(@PathVariable String shortUrl) {
        try {
            Url url = urlService.redirectToOriginal(shortUrl);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", url.getOriginalUrl())
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Short URL not found");
        }
    }
}
