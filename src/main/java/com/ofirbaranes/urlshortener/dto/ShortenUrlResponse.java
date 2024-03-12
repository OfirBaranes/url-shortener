package com.ofirbaranes.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortenUrlResponse {

    private String hashUrl;
    private boolean isNew;
}
