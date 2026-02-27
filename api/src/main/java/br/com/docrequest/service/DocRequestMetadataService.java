package br.com.docrequest.service;

import br.com.docrequest.config.RedisConfig;
import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.request.DocRequestMetadataCreateRequest;
import br.com.docrequest.dto.response.DocRequestMetadataResponse;
import br.com.docrequest.exception.ResourceNotFoundException;
import br.com.docrequest.mapper.DocRequestMetadataMapper;
import br.com.docrequest.repository.jpa.DocRequestMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DocRequestMetadataService {

    private final DocRequestMetadataRepository metadataRepository;
    private final DocRequestMetadataMapper metadataMapper;

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_METADATA_ACTIVE, key = "#request.name")
    public DocRequestMetadataResponse create(DocRequestMetadataCreateRequest request) {
        if (metadataRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                "DocRequestMetadata with name '" + request.getName() + "' already exists. Use update to create a new version.");
        }

        DocRequestMetadata metadata = metadataMapper.toEntity(request);
        metadata.setVersion(1);
        metadata.setEnabled(true);

        // Map and link fields
        for (int i = 0; i < request.getFields().size(); i++) {
            DocRequestFieldMetadata field = metadataMapper.toFieldEntity(request.getFields().get(i));
            field.setDocRequestMetadata(metadata);
            field.setFieldOrder(request.getFields().get(i).getFieldOrder() > 0
                ? request.getFields().get(i).getFieldOrder() : i);
            metadata.getFields().add(field);
        }

        DocRequestMetadata saved = metadataRepository.save(metadata);
        log.info("Created DocRequestMetadata: {} v{}", saved.getName(), saved.getVersion());
        return metadataMapper.toResponse(saved);
    }

    /**
     * Updates a template by disabling the current active version and creating a new one.
     * The new version number is currentMax + 1.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_METADATA_ACTIVE, key = "#name"),
        @CacheEvict(value = RedisConfig.CACHE_METADATA_VERSION, allEntries = true)
    })
    public DocRequestMetadataResponse update(String name, DocRequestMetadataCreateRequest request) {
        if (!metadataRepository.existsByName(name)) {
            throw ResourceNotFoundException.of("DocRequestMetadata", name);
        }

        // Disable current active version
        metadataRepository.disableActiveVersionByName(name);

        // Get next version number
        int nextVersion = metadataRepository.findMaxVersionByName(name).orElse(0) + 1;

        // Create new version
        DocRequestMetadata newVersion = metadataMapper.toEntity(request);
        newVersion.setName(name); // Keep the same logical name
        newVersion.setVersion(nextVersion);
        newVersion.setEnabled(true);

        for (int i = 0; i < request.getFields().size(); i++) {
            DocRequestFieldMetadata field = metadataMapper.toFieldEntity(request.getFields().get(i));
            field.setDocRequestMetadata(newVersion);
            field.setFieldOrder(request.getFields().get(i).getFieldOrder() > 0
                ? request.getFields().get(i).getFieldOrder() : i);
            newVersion.getFields().add(field);
        }

        DocRequestMetadata saved = metadataRepository.save(newVersion);
        log.info("Updated DocRequestMetadata: {} -> v{}", name, saved.getVersion());
        return metadataMapper.toResponse(saved);
    }

    @Cacheable(value = RedisConfig.CACHE_METADATA_ACTIVE, key = "#name")
    public DocRequestMetadataResponse findActiveByName(String name) {
        return metadataRepository.findByNameAndEnabledTrue(name)
            .map(metadataMapper::toResponse)
            .orElseThrow(() -> ResourceNotFoundException.of("DocRequestMetadata (active)", name));
    }

    public DocRequestMetadata findActiveEntityByName(String name) {
        return metadataRepository.findByNameAndEnabledTrue(name)
            .orElseThrow(() -> ResourceNotFoundException.of("DocRequestMetadata (active)", name));
    }

    @Cacheable(value = RedisConfig.CACHE_METADATA_VERSION, key = "#name + ':v:' + #version")
    public DocRequestMetadataResponse findByNameAndVersion(String name, int version) {
        return metadataRepository.findByNameAndVersion(name, version)
            .map(metadataMapper::toResponse)
            .orElseThrow(() -> ResourceNotFoundException.of("DocRequestMetadata", name + " v" + version));
    }

    public List<DocRequestMetadataResponse> findAllVersionsByName(String name) {
        return metadataRepository.findByNameOrderByVersionDesc(name).stream()
            .map(metadataMapper::toResponse)
            .collect(Collectors.toList());
    }

    public List<DocRequestMetadataResponse> findAllActive() {
        return metadataRepository.findByEnabledTrueOrderByNameAsc().stream()
            .map(metadataMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_METADATA_ACTIVE, key = "#name"),
        @CacheEvict(value = RedisConfig.CACHE_METADATA_VERSION, allEntries = true)
    })
    public void disable(String name) {
        if (!metadataRepository.existsByName(name)) {
            throw ResourceNotFoundException.of("DocRequestMetadata", name);
        }
        metadataRepository.disableAllVersionsByName(name);
        log.info("Disabled all versions of DocRequestMetadata: {}", name);
    }
}
