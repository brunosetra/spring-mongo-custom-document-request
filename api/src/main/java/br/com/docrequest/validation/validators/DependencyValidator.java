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
public class DependencyValidator implements FieldValidator {

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
        // Delegate to SpEL validation engine for dependency validation
        return spelValidationEngine.validateWithSpel(fieldMeta, value, resolvedFields, metadata);
    }

    @Override
    public DocRequestFieldType getType() {
        // This validator provides SpEL validation for dependency validation
        // It can be used for any field type that needs dependency validation
        // We return STRING as a default since dependency validation is not type-specific
        return DocRequestFieldType.STRING;
    }

    // Example dependency validation methods
    public boolean validateRequiredDependency(Object value, Object dependencyValue) {
        return value != null || dependencyValue == null;
    }

    public boolean validateMutualExclusion(Object value1, Object value2) {
        return !(value1 != null && value2 != null);
    }

    public boolean validateConditionalRequirement(Object value, Object condition, Object requiredValue) {
        return condition == null || condition.equals(requiredValue) ? value != null : true;
    }

    public boolean validateFieldComparison(Object value1, Object value2, String operator) {
        if (value1 == null || value2 == null) return true;
        
        switch (operator) {
            case "equals":
                return value1.equals(value2);
            case "notEquals":
                return !value1.equals(value2);
            case "greaterThan":
                if (value1 instanceof Number && value2 instanceof Number) {
                    return ((Number) value1).doubleValue() > ((Number) value2).doubleValue();
                }
                return false;
            case "lessThan":
                if (value1 instanceof Number && value2 instanceof Number) {
                    return ((Number) value1).doubleValue() < ((Number) value2).doubleValue();
                }
                return false;
            default:
                return true;
        }
    }

    public boolean validateRequiredForRole(Object value, String userRole, boolean required) {
        return !required || value != null;
    }

    public boolean validateConditionalField(Object value, String conditionField, Object conditionValue) {
        if (conditionField == null || conditionValue == null) return true;
        // This would be handled by SpEL expressions in practice
        return true;
    }
}