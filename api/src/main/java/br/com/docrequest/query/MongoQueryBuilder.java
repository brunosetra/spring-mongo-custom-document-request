package br.com.docrequest.query;

import br.com.docrequest.domain.enums.ComparisonOperator;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.domain.enums.LogicalOperator;
import br.com.docrequest.domain.enums.SortDirection;
import br.com.docrequest.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Builds MongoDB queries from parsed query representation.
 * Converts type-safe query DSL into MongoDB Criteria objects.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoQueryBuilder {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /**
     * Build a MongoDB query from parsed query.
     * Includes tenant isolation, template filter, and dynamic field filters.
     */
    public Query buildQuery(ParsedQuery parsedQuery) {
        Query query = new Query();
        
        // Add tenant isolation
        String partId = TenantContext.getCurrentTenant();
        query.addCriteria(Criteria.where("partId").is(partId));
        
        // Add template filter
        query.addCriteria(Criteria.where("docRequestMetadataName")
            .is(parsedQuery.getTemplateName()));
        
        // Add dynamic field filters
        Criteria filtersCriteria = buildFilterCriteria(parsedQuery.getFilters());
        if (filtersCriteria != null) {
            query.addCriteria(filtersCriteria);
        }
        
        // Add pagination
        query.skip((long) parsedQuery.getPagination().getPage() * 
            parsedQuery.getPagination().getSize());
        query.limit(parsedQuery.getPagination().getSize());
        
        // Add sorting
        for (ParsedPagination.SortConfig sort : parsedQuery.getPagination().getSort()) {
            String fieldPath = sort.getField().equals("createdAt") || 
                sort.getField().equals("updatedAt")
                ? sort.getField()
                : "fields." + sort.getField();
            
            query.with(Sort.by(
                sort.getDirection() == SortDirection.ASC 
                    ? Sort.Direction.ASC 
                    : Sort.Direction.DESC,
                fieldPath
            ));
        }
        
        return query;
    }
    
    /**
     * Build MongoDB Criteria from parsed filter group.
     * Handles nested logical operators (AND, OR, NOT).
     */
    private Criteria buildFilterCriteria(ParsedFilterGroup group) {
        if (group == null || group.getConditions() == null || 
            group.getConditions().isEmpty()) {
            return null;
        }
        
        Criteria[] criteriaArray = group.getConditions().stream()
            .map(this::buildConditionCriteria)
            .toArray(Criteria[]::new);
        
        return switch (group.getOperator()) {
            case AND -> new Criteria().andOperator(criteriaArray);
            case OR -> new Criteria().orOperator(criteriaArray);
            case NOT -> new Criteria().norOperator(criteriaArray);
        };
    }
    
    /**
     * Build MongoDB Criteria from a single filter condition.
     * Handles both leaf conditions and nested groups.
     */
    private Criteria buildConditionCriteria(ParsedFilterCondition condition) {
        // Handle nested group
        if (condition.getNestedGroup() != null) {
            return buildFilterCriteria(condition.getNestedGroup());
        }
        
        // Handle leaf condition
        String fieldPath = "fields." + condition.getField();
        Object value = convertValue(condition.getValue(), condition.getFieldType());
        
        return switch (condition.getOperator()) {
            case EQ -> Criteria.where(fieldPath).is(value);
            case NE -> Criteria.where(fieldPath).ne(value);
            case GT -> Criteria.where(fieldPath).gt(value);
            case GTE -> Criteria.where(fieldPath).gte(value);
            case LT -> Criteria.where(fieldPath).lt(value);
            case LTE -> Criteria.where(fieldPath).lte(value);
            case CONTAINS -> Criteria.where(fieldPath).regex(
                buildContainsRegex(value.toString()), "i"
            );
            case STARTS_WITH -> Criteria.where(fieldPath).regex(
                "^" + escapeRegex(value.toString()) + ".*", "i"
            );
            case ENDS_WITH -> Criteria.where(fieldPath).regex(
                ".*" + escapeRegex(value.toString()) + "$", "i"
            );
            case IN -> Criteria.where(fieldPath).in((List<?>) value);
            case NOT_IN -> Criteria.where(fieldPath).nin((List<?>) value);
            case IS_NULL -> Criteria.where(fieldPath).is(null);
            case IS_NOT_NULL -> Criteria.where(fieldPath).ne(null);
            case IS_EMPTY -> Criteria.where(fieldPath).is("");
            case IS_NOT_EMPTY -> Criteria.where(fieldPath).ne("").ne(null);
            case SIZE_EQ -> Criteria.where(fieldPath).size((Integer) value);
            case SIZE_GT -> Criteria.where(fieldPath).gt((Integer) value);
            case SIZE_LT -> Criteria.where(fieldPath).lt((Integer) value);
        };
    }
    
    /**
     * Convert value to appropriate type for MongoDB.
     * For date types (DATE, DATETIME, EXPIRATION_DATE), keeps as String in ISO format
     * since dates are now stored as ISO strings in MongoDB.
     * This enables direct string comparison which works correctly for ISO dates.
     */
    private Object convertValue(Object value, DocRequestFieldType fieldType) {
        if (value == null) {
            return null;
        }
        
        Object convertedValue;
        switch (fieldType) {
            case DATE, DATETIME, EXPIRATION_DATE -> {
                // Keep as String for ISO comparison - dates are stored as ISO strings in MongoDB
                // String comparison works correctly for ISO format dates (lexicographic order)
                convertedValue = value;
            }
            case INTEGER -> {
                if (value instanceof Long) {
                    convertedValue = ((Long) value).intValue();
                } else {
                    convertedValue = value;
                }
            }
            case DOUBLE -> {
                if (value instanceof Float) {
                    convertedValue = ((Float) value).doubleValue();
                } else {
                    convertedValue = value;
                }
            }
            default -> {
                convertedValue = value;
            }
        }
        return convertedValue;
    }
    
    /**
     * Build a case-insensitive contains regex.
     */
    private String buildContainsRegex(String value) {
        return ".*" + escapeRegex(value) + ".*";
    }
    
    /**
     * Escape special regex characters.
     */
    private String escapeRegex(String input) {
        return input.replaceAll("([.*+?^${}()|\\[\\]\\\\])", "\\\\$1");
    }
}
