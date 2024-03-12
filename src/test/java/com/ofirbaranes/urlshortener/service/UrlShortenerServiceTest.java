package com.ofirbaranes.urlshortener.service;

import com.ofirbaranes.urlshortener.dto.ShortenUrlResponse;
import com.ofirbaranes.urlshortener.entity.UrlShortening;
import com.ofirbaranes.urlshortener.exception.InvalidUrlException;
import com.ofirbaranes.urlshortener.exception.UrlHashNotFoundException;
import com.ofirbaranes.urlshortener.repository.UrlShortenerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UrlShortenerServiceTest {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @MockBean
    private UrlShortenerRepository urlShortenerRepository;

    @Test
    @DisplayName("shortenUrl - given invalid url, should throw exception")
    void givenInvalidUrl_ShouldThrowException() {
        String originalUrl = "notURL";

        assertThrows(InvalidUrlException.class, () -> urlShortenerService.shortenUrl(originalUrl));
    }

    @Test
    @DisplayName("shortenUrl - given existing url, should return the hash url, and be marked as not new")
    void givenExistingUrl_ShouldReturnHashUrlAndMarkedNotNew() {
        String urlHash = "123abc78";
        String originalUrl = "http://www.google.com";
        UrlShortening urlShortening = UrlShortening.builder().urlHash(urlHash).originalUrl(originalUrl).build();

        Mockito.when(urlShortenerRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(urlShortening));

        ShortenUrlResponse shortenUrlResponse = urlShortenerService.shortenUrl(originalUrl);

        assertEquals(shortenUrlResponse.getHashUrl(), urlHash);
        assertFalse(shortenUrlResponse.isNew());
    }


    @Test
    @DisplayName("shortenUrl - given non existing url, should return the hash url, and be marked as new")
    void givenNonExistingUrl_ShouldReturnHashUrlAndMarkedNew() {
        String urlHash = "123abc78";
        String originalUrl = "http://www.google.com";
        UrlShortening urlShortening = UrlShortening.builder().urlHash(urlHash).originalUrl(originalUrl).build();

        Mockito.when(urlShortenerRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        Mockito.when(urlShortenerRepository.save(ArgumentMatchers.any(UrlShortening.class))).thenReturn(urlShortening);

        ShortenUrlResponse shortenUrlResponse = urlShortenerService.shortenUrl(originalUrl);

        assertEquals(shortenUrlResponse.getHashUrl(), urlHash);
        assertTrue(shortenUrlResponse.isNew());
    }

    @Test
    @DisplayName("getOriginalUrl - given existing url hash, should return the original url")
    void givenExistingUrlHash_ShouldReturnTheOriginalUrl() {
        String urlHash = "123abc78";
        String originalUrl = "http://www.google.com";
        UrlShortening urlShortening = UrlShortening.builder().urlHash(urlHash).originalUrl(originalUrl).build();

        Mockito.when(urlShortenerRepository.findById(urlHash)).thenReturn(Optional.of(urlShortening));

        String returnedOriginalUrl = urlShortenerService.getOriginalUrl(urlHash);

        assertEquals(originalUrl, returnedOriginalUrl);
    }

    @Test
    @DisplayName("getOriginalUrl - given non existing url hash, should throw exception")
    void givenNonExistingUrlHash_ShouldThrowException() {
        String urlHash = "123abc78";

        Mockito.when(urlShortenerRepository.findById(urlHash)).thenReturn(Optional.empty());

        assertThrows(UrlHashNotFoundException.class, () -> urlShortenerService.getOriginalUrl(urlHash));
    }

}
