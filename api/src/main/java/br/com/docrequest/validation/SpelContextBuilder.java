package br.com.docrequest.validation;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpelContextBuilder {

    private final SpelExpressionParser expressionParser;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public StandardEvaluationContext buildValidationContext(
        DocRequestFieldMetadata fieldMeta,
        Object value,
        Map<String, Object> resolvedFields,
        DocRequestMetadata metadata
    ) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // Root object - the field value being validated
        context.setRootObject(value);
        
        // Variables for SpEL expressions
        context.setVariable("this", value);
        context.setVariable("field", fieldMeta);
        context.setVariable("metadata", metadata);
        context.setVariable("resolvedFields", resolvedFields);
        context.setVariable("tenant", TenantContext.getCurrentTenant());
        
        // Add system context
        context.setVariable("currentDate", new java.util.Date());
        context.setVariable("systemVersion", "1.0.0");
        
        return context;
    }

    public StandardEvaluationContext buildValidationContext(
        DocRequestFieldMetadata fieldMeta,
        Object value,
        Map<String, Object> resolvedFields,
        DocRequestMetadata metadata,
        String userId,
        Map<String, Object> additionalContext
    ) {
        StandardEvaluationContext context = buildValidationContext(fieldMeta, value, resolvedFields, metadata);
        
        // Add user context
        context.setVariable("userId", userId);
        context.setVariable("userRoles", additionalContext.get("roles"));
        context.setVariable("userPermissions", additionalContext.get("permissions"));
        
        // Add document context
        context.setVariable("documentType", metadata.getName());
        context.setVariable("documentEnabled", metadata.isEnabled());
        
        // Add additional context
        if (additionalContext != null) {
            additionalContext.forEach(context::setVariable);
        }
        
        return context;
    }
}