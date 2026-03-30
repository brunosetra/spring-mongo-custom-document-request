package br.com.docrequest.validation;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.response.FieldValidationError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpelValidationEngine {

    private final SpelContextBuilder contextBuilder;
    private final SpelExpressionParser expressionParser;

    /**
     * Validates a field value using SpEL expressions.
     *
     * @param fieldMeta the field metadata containing SpEL expressions
     * @param value the field value to validate
     * @param resolvedFields map of already resolved fields
     * @param metadata the complete document metadata
     * @return Optional<FieldValidationError> if validation fails
     */
    public Optional<FieldValidationError> validateWithSpel(
        DocRequestFieldMetadata fieldMeta,
        Object value,
        Map<String, Object> resolvedFields,
        DocRequestMetadata metadata
    ) {
        return validateWithSpel(fieldMeta, value, resolvedFields, metadata, null, null);
    }

    /**
     * Validates a field value using SpEL expressions with enhanced context.
     *
     * @param fieldMeta the field metadata containing SpEL expressions
     * @param value the field value to validate
     * @param resolvedFields map of already resolved fields
     * @param metadata the complete document metadata
     * @param userId current user ID
     * @param additionalContext additional context variables
     * @return Optional<FieldValidationError> if validation fails
     */
    public Optional<FieldValidationError> validateWithSpel(
        DocRequestFieldMetadata fieldMeta,
        Object value,
        Map<String, Object> resolvedFields,
        DocRequestMetadata metadata,
        String userId,
        Map<String, Object> additionalContext
    ) {
        if (!fieldMeta.isEnableSpelValidation()) {
            return Optional.empty();
        }

        try {
            StandardEvaluationContext context = contextBuilder.buildValidationContext(
                fieldMeta, value, resolvedFields, metadata, userId, additionalContext
            );

            // Evaluate conditional validation rules
            if (fieldMeta.getValidationExpression() != null && 
                !fieldMeta.getValidationExpression().isBlank()) {
                
                Boolean result = expressionParser.parseExpression(
                    fieldMeta.getValidationExpression()
                ).getValue(context, Boolean.class);
                
                if (Boolean.FALSE.equals(result)) {
                    return Optional.of(FieldValidationError.of(
                        fieldMeta.getName(),
                        "ERR_SPEL_VALIDATION_FAILED",
                        "Field validation failed for '" + fieldMeta.getName() + "'"
                    ));
                }
            }

            // Evaluate conditional field dependencies
            if (fieldMeta.getDependencyExpression() != null && 
                !fieldMeta.getDependencyExpression().isBlank()) {
                
                Boolean result = expressionParser.parseExpression(
                    fieldMeta.getDependencyExpression()
                ).getValue(context, Boolean.class);
                
                if (Boolean.FALSE.equals(result)) {
                    return Optional.of(FieldValidationError.of(
                        fieldMeta.getName(),
                        "ERR_DEPENDENCY_VALIDATION_FAILED",
                        "Field dependency validation failed for '" + fieldMeta.getName() + "'"
                    ));
                }
            }

            return Optional.empty();
            
        } catch (EvaluationException e) {
            log.error("SpEL evaluation error for field '{}': {}", fieldMeta.getName(), e.getMessage());
            return Optional.of(FieldValidationError.of(
                fieldMeta.getName(),
                "ERR_SPEL_EVALUATION_ERROR",
                "Error evaluating SpEL expression for field '" + fieldMeta.getName() + "': " + e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error during SpEL validation for field '{}': {}", fieldMeta.getName(), e.getMessage());
            return Optional.of(FieldValidationError.of(
                fieldMeta.getName(),
                "ERR_SPEL_VALIDATION_ERROR",
                "Unexpected error during SpEL validation for field '" + fieldMeta.getName() + "': " + e.getMessage()
            ));
        }
    }
}