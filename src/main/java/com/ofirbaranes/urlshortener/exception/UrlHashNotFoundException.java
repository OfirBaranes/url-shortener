package com.ofirbaranes.urlshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UrlHashNotFoundException extends RuntimeException {
    public UrlHashNotFoundException(String urlHash) {
        super("Could not find url hash " + urlHash);
    }
}
