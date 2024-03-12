package com.ofirbaranes.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofirbaranes.urlshortener.dto.ShortenUrlRequest;
import com.ofirbaranes.urlshortener.dto.ShortenUrlResponse;
import com.ofirbaranes.urlshortener.exception.InvalidUrlException;
import com.ofirbaranes.urlshortener.exception.UrlHashNotFoundException;
import com.ofirbaranes.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlShortenerController.class)
public class UrlShortenerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UrlShortenerService urlShortenerService;

    @Test
    @DisplayName("shortenUrl - given invalid original url, should return Http status BAD REQUEST")
    void givenInvalidOriginalUrl_shouldReturnHttpStatusBadRequest() throws Exception {
        String originalUrl = "notUrl";
        ShortenUrlRequest shortenUrlRequest = new ShortenUrlRequest(originalUrl);

        Mockito.when(urlShortenerService.shortenUrl(originalUrl)).thenThrow(new InvalidUrlException(originalUrl));

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(shortenUrlRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    @Test
    @DisplayName("shortenUrl - given existing original url, should return url hash marked as not new and Http status OK")
    void givenExistingOriginalUrl_shouldReturnUrlHashMarkedNotNewAndHttpStatusOk() throws Exception {
        String urlHash = "123abc78";
        String originalUrl = "http://www.google.com";
        ShortenUrlRequest shortenUrlRequest = new ShortenUrlRequest(originalUrl);
        ShortenUrlResponse shortenUrlResponse = new ShortenUrlResponse(urlHash, false);

        Mockito.when(urlShortenerService.shortenUrl(originalUrl)).thenReturn(shortenUrlResponse);

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(shortenUrlRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(asJsonString(shortenUrlResponse))))
                .andReturn();
    }


    @Test
    @DisplayName("shortenUrl - given non existing original url, should return url hash marked as new and Http status CREATED")
    void givenNonExistingOriginalUrl_shouldReturnUrlHashMarkedNewAndHttpStatusCreated() throws Exception {
        String urlHash = "123abc78";
        String originalUrl = "http://www.google.com";
        ShortenUrlRequest shortenUrlRequest = new ShortenUrlRequest(originalUrl);
        ShortenUrlResponse shortenUrlResponse = new ShortenUrlResponse(urlHash, true);

        Mockito.when(urlShortenerService.shortenUrl(originalUrl)).thenReturn(shortenUrlResponse);

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(shortenUrlRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(equalTo(asJsonString(shortenUrlResponse))))
                .andReturn();
    }


    @Test
    @DisplayName("getAndRedirectOriginalUrl - given existing url hash, should redirect to original url with Http status MOVED PERMANENTLY")
    void givenExistingUrlHash_shouldRedirectToOriginalUrlWithHttpStatusMovedPermanently() throws Exception {
        String urlHash = "123abc78";
        String originalUrl = "http://www.google.com";

        Mockito.when(urlShortenerService.getOriginalUrl(urlHash)).thenReturn(originalUrl);

        mockMvc.perform(get("/{urlHash}", urlHash)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl(originalUrl))
                .andReturn();
    }

    @Test
    @DisplayName("getAndRedirectOriginalUrl - given non existing url hash, should return Http status NOT FOUND")
    void givenNonExistingUrlHash_shouldReturnHttpStatusNotFound() throws Exception {
        String urlHash = "123abc78";

        Mockito.when(urlShortenerService.getOriginalUrl(urlHash)).thenThrow(new UrlHashNotFoundException(urlHash));

        mockMvc.perform(get("/{urlHash}", urlHash)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
