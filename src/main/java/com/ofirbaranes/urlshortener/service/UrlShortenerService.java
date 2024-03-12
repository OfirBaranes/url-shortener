package com.ofirbaranes.urlshortener.service;

import com.google.common.hash.Hashing;
import com.ofirbaranes.urlshortener.dto.ShortenUrlResponse;
import com.ofirbaranes.urlshortener.entity.UrlShortening;
import com.ofirbaranes.urlshortener.exception.InvalidUrlException;
import com.ofirbaranes.urlshortener.repository.UrlShortenerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ofirbaranes.urlshortener.exception.UrlHashNotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@Slf4j
public class UrlShortenerService {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    public ShortenUrlResponse shortenUrl(String originalUrl) {
        log.info("Validating url {}", originalUrl);
        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
        if (!urlValidator.isValid(originalUrl)) {
            log.warn("Url {} is not valid", originalUrl);
            throw new InvalidUrlException(originalUrl);
        }
        Optional<UrlShortening> optionalUrlShortening = urlShortenerRepository.findByOriginalUrl(originalUrl);
        if (optionalUrlShortening.isPresent()) {
            log.info("Url {} was already shortened with url hash {}", originalUrl, optionalUrlShortening.get().getUrlHash());
            return new ShortenUrlResponse(optionalUrlShortening.get().getUrlHash(), false);
        } else {
            String urlHash = createUrlHash(originalUrl);
            UrlShortening urlShortening = UrlShortening.builder().urlHash(urlHash).originalUrl(originalUrl).build();
            UrlShortening savedUrlShortening = urlShortenerRepository.save(urlShortening);
            log.info("Successfully shortened url {} to url hash {}", originalUrl, savedUrlShortening.getUrlHash());
            return new ShortenUrlResponse(savedUrlShortening.getUrlHash(), true);
        }
    }

    public String getOriginalUrl(String urlHash) {
        UrlShortening urlShortening = urlShortenerRepository.findById(urlHash)
                .orElseThrow(() -> {
                    log.warn("Could not find url hash " + urlHash);
                    throw new UrlHashNotFoundException(urlHash);
                });
        log.info("Found original url for url hash {} as {}", urlHash, urlShortening.getOriginalUrl());
        return urlShortening.getOriginalUrl();
    }

    private String createUrlHash(String originalUrl) {
        return Hashing.murmur3_32_fixed().hashString(originalUrl, StandardCharsets.UTF_8).toString();
    }

}
