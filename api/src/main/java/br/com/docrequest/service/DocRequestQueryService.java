package br.com.docrequest.service;

import br.com.docrequest.domain.document.DocRequest;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.request.QueryRequest;
import br.com.docrequest.dto.response.DocRequestResponse;
import br.com.docrequest.dto.response.QueryPagination;
import br.com.docrequest.dto.response.QueryResponse;
import br.com.docrequest.exception.InvalidQueryException;
import br.com.docrequest.query.MongoQueryBuilder;
import br.com.docrequest.query.ParsedQuery;
import br.com.docrequest.query.QueryParser;
import br.com.docrequest.util.DateFieldConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for executing advanced queries on DocRequest entities.
 * Orchestrates query parsing, building, and execution.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocRequestQueryService {
    
    private final QueryParser queryParser;
    private final MongoQueryBuilder queryBuilder;
    private final MongoTemplate mongoTemplate;
    
    /**
     * Execute a query request and return paginated results.
     * 
     * @param request The query request with filters and pagination
     * @return Query response with results and metadata
     * @throws InvalidQueryException if the query is invalid
     */
    public QueryResponse query(QueryRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Parse and validate query
            ParsedQuery parsedQuery = queryParser.parse(request);
            
            // Build MongoDB query
            org.springframework.data.mongodb.core.query.Query mongoQuery = 
                queryBuilder.buildQuery(parsedQuery);
            log.debug("{}", mongoQuery);
            // Execute query
            List<DocRequest> results = mongoTemplate.find(mongoQuery, DocRequest.class);
            
            // Get total count
            long total = mongoTemplate.count(
                mongoQuery.skip(0).limit(0), 
                DocRequest.class
            );
            
            // Create page
            int pageNumber = mongoQuery.getLimit() > 0 
                ? (int) (mongoQuery.getSkip() / mongoQuery.getLimit()) 
                : 0;
            int pageSize = mongoQuery.getLimit() > 0 ? mongoQuery.getLimit() : 10;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<DocRequest> page = new PageImpl<>(results, pageable, total);
            
            // Convert to response DTOs
            DocRequestMetadata metadata = parsedQuery.getMetadata();
            List<DocRequestResponse> responseResults = page.getContent().stream()
                .map(doc -> toResponse(doc, metadata))
                .collect(Collectors.toList());
            
            QueryPagination pagination = QueryPagination.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("Query executed in {}ms, returned {} of {} results",
                executionTime, results.size(), total);
            
            return QueryResponse.builder()
                .results(responseResults)
                .pagination(pagination)
                .executionTimeMs(executionTime)
                .build();
            
        } catch (InvalidQueryException e) {
            log.error("Query validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Query execution failed", e);
            throw new InvalidQueryException(
                "Query execution failed: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Convert DocRequest entity to response DTO.
     */
    private DocRequestResponse toResponse(DocRequest docRequest, DocRequestMetadata metadata) {
        // Convert date fields from ISO format to template format
        Map<String, Object> fieldsForResponse = DateFieldConverter.convertToTemplateFormat(
            docRequest.getFields(), 
            metadata
        );
        
        return DocRequestResponse.builder()
            .uuid(docRequest.getUuid())
            .partId(docRequest.getPartId())
            .docRequestMetadataName(docRequest.getDocRequestMetadataName())
            .docRequestMetadataVersion(docRequest.getDocRequestMetadataVersion())
            .fields(fieldsForResponse)
            .createdAt(docRequest.getCreatedAt())
            .updatedAt(docRequest.getUpdatedAt())
            .build();
    }
}
