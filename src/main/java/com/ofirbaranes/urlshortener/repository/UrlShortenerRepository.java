package com.ofirbaranes.urlshortener.repository;

import com.ofirbaranes.urlshortener.entity.UrlShortening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlShortenerRepository extends JpaRepository<UrlShortening, String> {

    Optional<UrlShortening> findByOriginalUrl(String OriginalUrl);
}
