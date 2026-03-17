# DocRequestMetadata Validation Improvements

## Overview

This document describes the improvements made to the DocRequestMetadata validation system, following SOLID principles, Clean Code, KISS Principle, and other best practices.

## Problems with Original Code

The original validation code had several issues:

1. **Single Responsibility Principle (SRP) Violation**: The `validateMetaFields` method handled too many concerns - field count, date formats, default values, type/input type compatibility, editable flags, domain tables, references, highlights, and duplicates.

2. **Open/Closed Principle (OCP) Violation**: Adding new validation rules required modifying the existing monolithic method.

3. **Code Duplication**: The `isCalculatedInputTypeReferenceValid` and `isDomainCalculatedInputTypeReferenceValid` methods were nearly identical (~90% duplicate code).

4. **Long Method**: The `validateMetaFields` method was over 100 lines with deeply nested conditionals.

5. **Magic Strings**: Error messages were hardcoded throughout the code.

6. **Complex Conditional Logic**: Many nested if statements made the code hard to follow and maintain.

7. **Inconsistent Error Handling**: Some validations threw exceptions immediately, while others collected errors.

8. **Poor Naming**: The method `validarFormatoDaData` was in Portuguese while the rest was in English.

9. **No Clear Separation**: Field-level, cross-field, and metadata-level validations were all mixed together.

## Solution Architecture

The new validation system follows these design patterns:

### 1. Strategy Pattern

Each validation rule is implemented as a separate `MetadataValidator` strategy, allowing easy addition of new validators without modifying existing code.

### 2. Chain of Responsibility Pattern

The `DocRequestMetadataValidator` orchestrator executes validators in order, collecting all errors before throwing an exception.

### 3. Single Responsibility Principle

Each validator handles one specific validation concern:

- `FieldCountValidator` - Validates fields list is not empty
- `DateFormatValidator` - Validates date format strings
- `DefaultValueValidator` - Validates defaultValue for FIXED/DEFAULT types
- `TypeInputTypeCompatibilityValidator` - Validates type/input type compatibility
- `EditableValidator` - Validates editable flag based on input type
- `DomainTableValidator` - Validates domain table references
- `ReferenceValidator` - Validates field references in CALCULATED/DOMAIN_CALCULATED
- `UniqueFieldTypeValidator` - Validates unique field types (CPF, EMAIL, EXPIRATION_DATE)

### 4. DRY Principle (Don't Repeat Yourself)

The `ReferenceValidationHelper` class extracts common logic for validating field references, eliminating code duplication.

### 5. Dependency Injection

All validators are Spring components with `@Component` annotation, automatically discovered and injected.

## New Files Created

### Exception and DTO

- `InvalidDocRequestMetadataException` - Custom exception for validation errors
- `MetadataValidationError` - DTO for validation error details

### Core Validation

- `MetadataValidator` - Interface for all validators
- `DocRequestMetadataValidator` - Main orchestrator

### Field-Level Validators

- `FieldCountValidator` (Order 10) - Validates fields list is not empty
- `DateFormatValidator` (Order 20) - Validates date format strings
- `DefaultValueValidator` (Order 30) - Validates defaultValue for FIXED/DEFAULT types
- `TypeInputTypeCompatibilityValidator` (Order 40) - Validates type/input type compatibility
- `EditableValidator` (Order 50) - Validates editable flag based on input type
- `DomainTableValidator` (Order 60) - Validates domain table references

### Cross-Field Validators

- `ReferenceValidator` (Order 80) - Validates field references in CALCULATED/DOMAIN_CALCULATED
- `ReferenceValidationHelper` - Helper class to eliminate code duplication

### Metadata-Level Validators

- `UniqueFieldTypeValidator` (Order 90) - Validates unique field types

## Benefits

### 1. Maintainability

- Each validator is small and focused (typically 30-60 lines)
- Easy to understand what each validator does
- Changes to one validator don't affect others

### 2. Extensibility

- Adding a new validation rule is as simple as creating a new class implementing `MetadataValidator`
- No need to modify existing validators
- Spring automatically discovers and injects new validators

### 3. Testability

- Each validator can be unit tested independently
- Mock dependencies easily
- Clear input/output contracts

### 4. Readability

- Clear method names that describe what they validate
- Consistent error message format
- Well-documented with JavaDoc

### 5. Error Aggregation

- All validation errors are collected before throwing
- Users see all errors at once, not one at a time
- Better user experience

### 6. Performance

- Validators are ordered by dependency (e.g., field count before field references)
- Early validation failures prevent unnecessary processing
- No redundant checks

## Integration with DocRequestMetadataService

The validator is integrated into both `create` and `update` methods:

```java
// Validate metadata before saving
metadataValidator.validate(metadata);
```

This ensures all metadata is validated before persistence, maintaining data integrity.

## Validation Order

Validators execute in this order:

1. **FieldCountValidator** (10) - Basic structure check
2. **DateFormatValidator** (20) - Date format validation
3. **DefaultValueValidator** (30) - Default value validation
4. **TypeInputTypeCompatibilityValidator** (40) - Type compatibility
5. **EditableValidator** (50) - Editable flag validation
6. **DomainTableValidator** (60) - Domain table existence
7. **ReferenceValidator** (80) - Field reference validation
8. **UniqueFieldTypeValidator** (90) - Unique type validation

## Future Enhancements

Potential improvements that could be added:

3. **Custom Validators** - Allow users to define custom validation rules
4. **Validation Groups** - Support different validation contexts (create vs update)
5. **Async Validation** - For expensive validations (e.g., external service calls)

## Migration Notes

The original validation code can be removed from the service class. The new system provides equivalent functionality with better structure and maintainability.

## Testing Recommendations

Each validator should have unit tests covering:

- Valid inputs that pass validation
- Invalid inputs that fail validation
- Edge cases (null, empty, boundary values)
- Error message accuracy

Example test structure:

```java
@Test
void validate_WhenFieldsIsEmpty_ReturnsError() {
    DocRequestMetadata metadata = DocRequestMetadata.builder()
        .fields(Collections.emptyList())
        .build();

    List<MetadataValidationError> errors = validator.validate(metadata);

    assertFalse(errors.isEmpty());
    assertEquals("DocRequestMetadata.fields must not be empty", errors.get(0).getMessage());
}
```

## Conclusion

The new validation system provides a robust, maintainable, and extensible foundation for validating DocRequestMetadata. It follows industry best practices and design patterns, making the codebase easier to understand, test, and extend.
