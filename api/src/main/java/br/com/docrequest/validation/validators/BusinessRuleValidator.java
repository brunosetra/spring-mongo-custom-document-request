package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.FieldValidator;
import br.com.docrequest.validation.SpelValidationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BusinessRuleValidator implements FieldValidator {

    private final SpelValidationEngine spelValidationEngine;

    @Override
    public Optional<FieldValidationError> validate(
        String fieldName, 
        Object value, 
        DocRequestFieldMetadata fieldMeta
    ) {
        // Traditional validation can be implemented here if needed
        return Optional.empty();
    }

    @Override
    public Optional<FieldValidationError> validateWithSpel(
        String fieldName,
        Object value,
        DocRequestFieldMetadata fieldMeta,
        Map<String, Object> resolvedFields,
        DocRequestMetadata metadata
    ) {
        // Delegate to SpEL validation engine for complex business rules
        return spelValidationEngine.validateWithSpel(fieldMeta, value, resolvedFields, metadata);
    }

    @Override
    public DocRequestFieldType getType() {
        // This validator doesn't handle a specific field type but provides SpEL validation
        // It can be used as a general validator for any field type that needs SpEL validation
        return DocRequestFieldType.STRING; // Or we could create a new enum type for SpEL validation
    }

    // Example business rule methods that can be called from SpEL
    public boolean validateDocumentType(Object value, String documentType) {
        // Implement document type-specific validation logic
        log.debug("Validating document type: {} for value: {}", documentType, value);
        return true;
    }

    public boolean validateDateRange(Object value, Object startDate, Object endDate) {
        // Implement date range validation logic
        log.debug("Validating date range for value: {}, startDate: {}, endDate: {}", value, startDate, endDate);
        return true;
    }

    public boolean validateFileSize(Object value, Long maxSize, String userRole) {
        // Implement file size validation based on user role
        log.debug("Validating file size for value: {}, maxSize: {}, userRole: {}", value, maxSize, userRole);
        return true;
    }

    public boolean validateRequiredForDocumentType(Object value, String documentType, boolean required) {
        // Implement conditional requirement based on document type
        log.debug("Validating required field for document type: {}, required: {}, value: {}", documentType, required, value);
        return !required || value != null;
    }

    public boolean validatePasswordStrength(Object value, String userRole) {
        // Implement password strength validation based on user role
        if (value == null) return true;
        String password = value.toString();
        
        // Basic password strength validation
        boolean hasLength = password.length() >= 8;
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasNumber = password.matches(".*\\d.*");
        
        boolean isStrong = hasLength && hasUpperCase && hasLowerCase && hasNumber;
        
        // Premium users might have stronger requirements
        if ("PREMIUM".equals(userRole)) {
            return isStrong && password.matches(".*[!@#$%^&*].*");
        }
        
        return isStrong;
    }
}