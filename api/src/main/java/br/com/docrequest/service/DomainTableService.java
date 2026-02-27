package br.com.docrequest.service;

import br.com.docrequest.config.RedisConfig;
import br.com.docrequest.domain.entity.DomainTable;
import br.com.docrequest.domain.entity.DomainTableRow;
import br.com.docrequest.dto.request.DomainTableCreateRequest;
import br.com.docrequest.dto.response.DomainTableResponse;
import br.com.docrequest.exception.ResourceNotFoundException;
import br.com.docrequest.mapper.DomainTableMapper;
import br.com.docrequest.repository.jpa.DomainTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DomainTableService {

    private final DomainTableRepository domainTableRepository;
    private final DomainTableMapper domainTableMapper;

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DOMAIN_TABLE, key = "#request.name")
    public DomainTableResponse create(DomainTableCreateRequest request) {
        if (domainTableRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Domain table with name '" + request.getName() + "' already exists");
        }

        DomainTable domainTable = domainTableMapper.toEntity(request);

        // Add rows
        if (request.getRows() != null) {
            for (Map<String, String> rowValues : request.getRows()) {
                DomainTableRow row = DomainTableRow.builder()
                    .domainTable(domainTable)
                    .values(rowValues)
                    .build();
                domainTable.getRows().add(row);
            }
        }

        DomainTable saved = domainTableRepository.save(domainTable);
        log.info("Created domain table: {}", saved.getName());
        return domainTableMapper.toResponse(saved);
    }

    @Cacheable(value = RedisConfig.CACHE_DOMAIN_TABLE, key = "#name")
    public DomainTableResponse findByNameAsResponse(String name) {
        DomainTable domainTable = findByName(name);
        return domainTableMapper.toResponse(domainTable);
    }

    public DomainTable findByName(String name) {
        return domainTableRepository.findByNameWithRows(name)
            .orElseThrow(() -> ResourceNotFoundException.of("DomainTable", name));
    }

    public List<DomainTableResponse> findAll() {
        return domainTableRepository.findAllByOrderByNameAsc().stream()
            .map(domainTableMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DOMAIN_TABLE, key = "#name")
    public DomainTableResponse update(String name, DomainTableCreateRequest request) {
        DomainTable existing = findByName(name);

        existing.setDescription(request.getDescription());
        existing.setColumnId(request.getColumnId());
        existing.setColumns(request.getColumns());

        // Replace all rows
        existing.getRows().clear();
        if (request.getRows() != null) {
            for (Map<String, String> rowValues : request.getRows()) {
                DomainTableRow row = DomainTableRow.builder()
                    .domainTable(existing)
                    .values(rowValues)
                    .build();
                existing.getRows().add(row);
            }
        }

        DomainTable saved = domainTableRepository.save(existing);
        log.info("Updated domain table: {}", saved.getName());
        return domainTableMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DOMAIN_TABLE, key = "#name")
    public void delete(String name) {
        DomainTable domainTable = findByName(name);
        domainTableRepository.delete(domainTable);
        log.info("Deleted domain table: {}", name);
    }

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DOMAIN_TABLE, key = "#name")
    public DomainTableResponse addRows(String name, List<Map<String, String>> rows) {
        DomainTable domainTable = findByName(name);

        for (Map<String, String> rowValues : rows) {
            DomainTableRow row = DomainTableRow.builder()
                .domainTable(domainTable)
                .values(rowValues)
                .build();
            domainTable.getRows().add(row);
        }

        DomainTable saved = domainTableRepository.save(domainTable);
        return domainTableMapper.toResponse(saved);
    }
}
