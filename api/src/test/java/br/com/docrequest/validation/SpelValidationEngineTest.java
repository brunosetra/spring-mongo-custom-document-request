package br.com.docrequest.validation;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpelValidationEngineTest {

    @Mock
    private SpelContextBuilder contextBuilder;

    private SpelValidationEngine spelValidationEngine;

    @BeforeEach
    void setUp() {
        spelValidationEngine = new SpelValidationEngine(contextBuilder, new SpelExpressionParser());
    }

    @Test
    void testSpelDisabled() {
        DocRequestFieldMetadata fieldMeta = new DocRequestFieldMetadata();
        fieldMeta.setName("testField");
        fieldMeta.setEnableSpelValidation(false);

        Map<String, Object> resolvedFields = new HashMap<>();
        DocRequestMetadata metadata = new DocRequestMetadata();

        Optional<FieldValidationError> result = spelValidationEngine.validateWithSpel(
            fieldMeta, "anyValue", resolvedFields, metadata
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void testConditionalValidationSuccess() {
        DocRequestFieldMetadata fieldMeta = new DocRequestFieldMetadata();
        fieldMeta.setName("testField");
        fieldMeta.setInputType(DocRequestFieldInputType.IN);
        fieldMeta.setType(DocRequestFieldType.STRING);
        fieldMeta.setEnableSpelValidation(true);
        fieldMeta.setValidationExpression("#this.length() > 5");

        Map<String, Object> resolvedFields = new HashMap<>();
        DocRequestMetadata metadata = new DocRequestMetadata();

        var context = new org.springframework.expression.spel.support.StandardEvaluationContext();
        context.setRootObject("longValue");
        context.setVariable("this", "longValue");
        context.setVariable("field", fieldMeta);
        context.setVariable("metadata", metadata);
        context.setVariable("resolvedFields", resolvedFields);


        when(contextBuilder.buildValidationContext(any(),any(), any(), any(), any(), any())).thenReturn(context);

        Optional<FieldValidationError> result = spelValidationEngine.validateWithSpel(
            fieldMeta, "longValue", resolvedFields, metadata
        );

        assertTrue(result.isEmpty());
        verify(contextBuilder).buildValidationContext(any(),any(), any(), any(), any(), any());
    }

    @Test
    void testConditionalValidationFailure() {
        DocRequestFieldMetadata fieldMeta = new DocRequestFieldMetadata();
        fieldMeta.setName("testField");
        fieldMeta.setEnableSpelValidation(true);
        fieldMeta.setValidationExpression("#this.length() > 5");

        Map<String, Object> resolvedFields = new HashMap<>();
        DocRequestMetadata metadata = new DocRequestMetadata();

        var context = new org.springframework.expression.spel.support.StandardEvaluationContext();
        context.setRootObject("short");
        context.setVariable("this", "short");
        context.setVariable("field", fieldMeta);
        context.setVariable("metadata", metadata);
        context.setVariable("resolvedFields", resolvedFields);
        when(contextBuilder.buildValidationContext(any(),any(), any(), any(), any(), any())).thenReturn(context);

        Optional<FieldValidationError> result = spelValidationEngine.validateWithSpel(
            fieldMeta, "short", resolvedFields, metadata
        );

        assertTrue(result.isPresent());
        assertEquals("ERR_SPEL_VALIDATION_FAILED", result.get().getErrorCode());
        assertEquals("Field validation failed for 'testField'", result.get().getMessage());
        verify(contextBuilder).buildValidationContext(any(),any(), any(), any(), any(), any());
    }

    @Test
    void testDependencyValidationSuccess() {
        DocRequestFieldMetadata fieldMeta = new DocRequestFieldMetadata();
        fieldMeta.setName("testField");
        fieldMeta.setEnableSpelValidation(true);
        fieldMeta.setDependencyExpression("#otherField == 'SPECIFIC_VALUE' ? #this != null : true");

        Map<String, Object> resolvedFields = new HashMap<>();
        resolvedFields.put("otherField", "SPECIFIC_VALUE");
        DocRequestMetadata metadata = new DocRequestMetadata();

        var context = new org.springframework.expression.spel.support.StandardEvaluationContext();
        context.setRootObject("value");
        context.setVariable("this", "value");
        context.setVariable("field", fieldMeta);
        context.setVariable("metadata", metadata);
        context.setVariable("resolvedFields", resolvedFields);
        when(contextBuilder.buildValidationContext(any(),any(), any(), any(), any(), any())).thenReturn(context);

        Optional<FieldValidationError> result = spelValidationEngine.validateWithSpel(
            fieldMeta, "value", resolvedFields, metadata
        );

        assertTrue(result.isEmpty());
        verify(contextBuilder).buildValidationContext(any(), any(), any(), any(), any(), any());
    }

    // @Test
    // void testDependencyValidationFailure() {
    //     DocRequestFieldMetadata fieldMeta = new DocRequestFieldMetadata();
    //     fieldMeta.setName("testField");
    //     fieldMeta.setEnableSpelValidation(true);
    //     fieldMeta.setDependencyExpression("#otherField == 'SPECIFIC_VALUE' ? #this != null : true");

    //     Map<String, Object> resolvedFields = new HashMap<>();
    //     resolvedFields.put("otherField", "SPECIFIC_VALUE");
    //     DocRequestMetadata metadata = new DocRequestMetadata();

    //     var context = new org.springframework.expression.spel.support.StandardEvaluationContext();
    //     context.setRootObject(null);
    //     context.setVariable("this", null);
    //     context.setVariable("field", fieldMeta);
    //     context.setVariable("metadata", metadata);
    //     context.setVariable("resolvedFields", resolvedFields);
    //     when(contextBuilder.buildValidationContext(any(),any(), any(), any(), any(), any())).thenReturn(context);


    //     Optional<FieldValidationError> result = spelValidationEngine.validateWithSpel(
    //         fieldMeta, null, resolvedFields, metadata
    //     );

    //     assertTrue(result.isPresent());
    //     assertEquals("ERR_DEPENDENCY_VALIDATION_FAILED", result.get().getErrorCode());
    //     assertEquals("Field dependency validation failed for 'testField'", result.get().getMessage());
    //     verify(contextBuilder).buildValidationContext(any(),any(), any(), any(), any(), any());
    // }

    @Test
    void testSpelEvaluationError() {
        DocRequestFieldMetadata fieldMeta = new DocRequestFieldMetadata();
        fieldMeta.setName("testField");
        fieldMeta.setEnableSpelValidation(true);
        fieldMeta.setValidationExpression("invalidSpelExpression");

        Map<String, Object> resolvedFields = new HashMap<>();
        DocRequestMetadata metadata = new DocRequestMetadata();

        var context = new org.springframework.expression.spel.support.StandardEvaluationContext();
        context.setRootObject("value");
        context.setVariable("this", "value");
        context.setVariable("field", fieldMeta);
        context.setVariable("metadata", metadata);
        context.setVariable("resolvedFields", resolvedFields);
        when(contextBuilder.buildValidationContext(any(),any(), any(), any(), any(), any())).thenReturn(context);

        Optional<FieldValidationError> result = spelValidationEngine.validateWithSpel(
            fieldMeta, "value", resolvedFields, metadata
        );

        assertTrue(result.isPresent());
        assertEquals("ERR_SPEL_EVALUATION_ERROR", result.get().getErrorCode());
        assertTrue(result.get().getMessage().contains("Error evaluating SpEL expression"));
        verify(contextBuilder).buildValidationContext(any(), any(), any(), any(), any(), any());
    }

    // @Test
    // void testEnhancedContext() {
    //     DocRequestFieldMetadata fieldMeta = new DocRequestFieldMetadata();
    //     fieldMeta.setName("testField");
    //     fieldMeta.setEnableSpelValidation(true);
    //     fieldMeta.setValidationExpression("#userId != null && #tenant != null");

    //     Map<String, Object> resolvedFields = new HashMap<>();
    //     DocRequestMetadata metadata = new DocRequestMetadata();

    //     Map<String, Object> additionalContext = new HashMap<>();
    //     additionalContext.put("userId", "testUser");
    //     additionalContext.put("tenant", "testTenant");

    //     when(contextBuilder.buildValidationContext(any(), any(), any(), any(), any(), any())).thenReturn(new org.springframework.expression.spel.support.StandardEvaluationContext());

    //     Optional<FieldValidationError> result = spelValidationEngine.validateWithSpel(
    //         fieldMeta, "value", resolvedFields, metadata, "testUser", additionalContext
    //     );

    //     assertTrue(result.isEmpty());
    //     verify(contextBuilder).buildValidationContext(any(), any(), any(), any(), eq("testUser"), eq(additionalContext));
    // }

    // @Test
    // void testBothExpressionsEnabled() {
    //     DocRequestFieldMetadata fieldMeta = new DocRequestFieldMetadata();
    //     fieldMeta.setName("testField");
    //     fieldMeta.setEnableSpelValidation(true);
    //     fieldMeta.setValidationExpression("#this.length() > 3");
    //     fieldMeta.setDependencyExpression("#otherField != null");

    //     Map<String, Object> resolvedFields = new HashMap<>();
    //     resolvedFields.put("otherField", "someValue");
    //     DocRequestMetadata metadata = new DocRequestMetadata();

    //     when(contextBuilder.buildValidationContext(any(), any(), any(), any())).thenReturn(new org.springframework.expression.spel.support.StandardEvaluationContext());

    //     Optional<FieldValidationError> result = spelValidationEngine.validateWithSpel(
    //         fieldMeta, "valid", resolvedFields, metadata
    //     );

    //     assertTrue(result.isEmpty());
    //     verify(contextBuilder).buildValidationContext(any(), any(), any(), any());
    // }

    // @Test
    // void testBothExpressionsFail() {
    //     DocRequestFieldMetadata fieldMeta = new DocRequestFieldMetadata();
    //     fieldMeta.setName("testField");
    //     fieldMeta.setEnableSpelValidation(true);
    //     fieldMeta.setValidationExpression("#this.length() > 10");
    //     fieldMeta.setDependencyExpression("#otherField == 'required'");

    //     Map<String, Object> resolvedFields = new HashMap<>();
    //     resolvedFields.put("otherField", "not_required");
    //     DocRequestMetadata metadata = new DocRequestMetadata();

    //     when(contextBuilder.buildValidationContext(any(), any(), any(), any())).thenReturn(new org.springframework.expression.spel.support.StandardEvaluationContext());

    //     Optional<FieldValidationError> result = spelValidationEngine.validateWithSpel(
    //         fieldMeta, "short", resolvedFields, metadata
    //     );

    //     assertTrue(result.isPresent());
    //     // Should fail on validation expression first
    //     assertEquals("ERR_SPEL_VALIDATION_FAILED", result.get().getErrorCode());
    //     verify(contextBuilder).buildValidationContext(any(), any(), any(), any());
    // }
}