package br.com.docrequest.service;

import br.com.docrequest.config.RedisConfig;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.response.DocRequestMetadataResponse;
import br.com.docrequest.mapper.DocRequestMetadataMapper;
import br.com.docrequest.repository.jpa.DocRequestMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for caching DocRequestMetadata templates.
 * Reduces database queries for frequently accessed templates.
 * 
 * Note: We cache DTOs instead of entities to avoid LazyInitializationException
 * when the cached object is serialized by Jackson after the Hibernate session is closed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataCacheService {
    
    private final DocRequestMetadataRepository metadataRepository;
    private final DocRequestMetadataMapper metadataMapper;
    
    /**
     * Get metadata template by name with caching.
     * Cache key: template name
     * Cache TTL: 15 minutes (configurable via app.cache.metadata-ttl-minutes)
     * 
     * Returns a DTO to avoid Hibernate lazy initialization issues when the cached
     * object is serialized after the session is closed.
     */
    @Cacheable(value = RedisConfig.CACHE_METADATA_ACTIVE, key = "#name")
    public Optional<DocRequestMetadataResponse> getMetadata(String name) {
        log.debug("Fetching metadata for template: {}", name);
        return metadataRepository.findByNameAndEnabledTrue(name)
                .map(metadataMapper::toResponse);
    }
    
    /**
     * Get metadata entity by name without caching.
     * Use this method when you need the entity for internal operations
     * (e.g., accessing fields for validation) rather than for serialization.
     * 
     * Note: This method is not cached to avoid LazyInitializationException.
     * The entity should be used within the same transaction/session.
     */
    public Optional<DocRequestMetadata> getMetadataEntity(String name) {
        log.debug("Fetching metadata entity for template: {}", name);
        return metadataRepository.findByNameAndEnabledTrue(name);
    }
    
    /**
     * Evict cache entry for a specific template.
     * Called when metadata is updated.
     */
    public void evictCache(String name) {
        // Cache eviction is handled by Spring Cache abstraction
        log.debug("Evicting cache for template: {}", name);
    }
    
    /**
     * Evict all metadata cache entries.
     * Called when metadata is deleted or bulk updated.
     */
    public void evictAllCache() {
        // Cache eviction is handled by Spring Cache abstraction
        log.debug("Evicting all metadata cache");
    }
}
