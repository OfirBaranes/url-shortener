package com.ofirbaranes.urlshortener.controller;

import com.ofirbaranes.urlshortener.dto.ShortenUrlRequest;
import com.ofirbaranes.urlshortener.dto.ShortenUrlResponse;
import com.ofirbaranes.urlshortener.service.UrlShortenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@Slf4j
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @PostMapping
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody ShortenUrlRequest shortenUrlRequest) {
        log.info("Received url to shorten " + shortenUrlRequest.getOriginalUrl());
        ShortenUrlResponse shortenUrlResponse = urlShortenerService.shortenUrl(shortenUrlRequest.getOriginalUrl());
        if (shortenUrlResponse.isNew())
            return ResponseEntity.status(HttpStatus.CREATED).body(shortenUrlResponse);
        else
            return ResponseEntity.status(HttpStatus.OK).body(shortenUrlResponse);
    }

    @GetMapping("/{urlHash}")
    public ResponseEntity<Void> getAndRedirectOriginalUrl(@PathVariable String urlHash) throws URISyntaxException {
        log.info("Received url hash {}, looking for the original url", urlHash);
        String originalUrl = urlShortenerService.getOriginalUrl(urlHash);
        log.info("Redirecting to original url " + originalUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI(originalUrl));
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);

    }
}
