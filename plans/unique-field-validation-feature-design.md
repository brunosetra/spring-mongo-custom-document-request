# Unique Field Validation Feature Design

## Feature Overview

As a user that creates templates, I would like to have the possibility to create a DocRequestFieldMetadata that has unique value for the template.

**Example**: A template named "student", the field "social-security-number" is UNIQUE. So, if there a document persisted with "social-security-number" with value "12345", when I try to request to save another DocRequest with "12345" in field "social-security-number", API must deny the request.

## Requirements

1. **Template Configuration**: Allow marking specific fields as unique in the DocRequestFieldMetadata
2. **Validation Logic**: Validate uniqueness at the time of DocRequest creation
3. **Multi-tenant Support**: Uniqueness should be scoped to tenant (partId)
4. **Template Scope**: Uniqueness is enforced within the same template name only
5. **Error Handling**: Provide clear error messages when uniqueness constraint is violated

## Implementation Options

### Option 1: Database-Level Unique Constraint with Custom Validator

**Approach**: Add a unique constraint at the database level and implement a custom validator that checks for duplicates before insertion.

#### Architecture Changes

1. **Entity Updates**:
   - Add `unique` boolean field to `DocRequestFieldMetadata`
   - Add compound MongoDB index for unique fields

2. **Repository Updates**:
   - Add custom query method to check for existing values
   - Implement uniqueness check before save

3. **Validator Updates**:
   - Create `UniqueFieldValueValidator` that checks database for duplicates
   - Integrate with existing `DocRequestValidationEngine`

#### Implementation Details

**Step 1: Update DocRequestFieldMetadata Entity**

```java
@Column(name = "unique", nullable = false)
@Builder.Default
private boolean unique = false;
```

**Step 2: Update DocRequest Repository**

```java
@Repository
public interface DocRequestRepository extends MongoRepository<DocRequest, String> {

    // Existing methods...

    /**
     * Check if a unique field value already exists for the given template and tenant
     */
    boolean existsByPartIdAndDocRequestMetadataNameAndFieldsContainingValue(
        String partId,
        String metadataName,
        String fieldName,
        Object value
    );

    /**
     * Find existing document with specific field value
     */
    Optional<DocRequest> findByPartIdAndDocRequestMetadataNameAndFields(
        String partId,
        String metadataName,
        Map<String, Object> fields
    );
}
```

**Step 3: Create Unique Field Validator**

```java
@Component
public class UniqueFieldValueValidator implements FieldValidator {

    private final DocRequestRepository docRequestRepository;

    @Override
    public Optional<FieldValidationError> validate(String fieldName, Object value, DocRequestFieldMetadata fieldMeta) {
        if (!fieldMeta.isUnique() || value == null) {
            return Optional.empty();
        }

        // Check if this value already exists for this template and tenant
        boolean exists = docRequestRepository.existsByPartIdAndDocRequestMetadataNameAndFieldsContainingValue(
            TenantContext.getCurrentTenant(),
            fieldMeta.getDocRequestMetadata().getName(),
            fieldName,
            value
        );

        if (exists) {
            return Optional.of(FieldValidationError.of(
                fieldName,
                "ERR_UNIQUE_FIELD_VIOLATION",
                "Field '" + fieldName + "' must have a unique value. The value '" + value + "' already exists."
            ));
        }

        return Optional.empty();
    }

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.STRING; // Applies to all field types
    }
}
```

**Step 4: Update DTO**

```java
public class DocRequestFieldMetadataRequest {
    // Existing fields...

    private boolean unique = false;
}
```

#### Pros

- **Data Integrity**: Database-level constraint ensures uniqueness even if application logic fails
- **Performance**: Database indexes provide fast uniqueness checks
- **Consistency**: Single source of truth for uniqueness constraints
- **Scalability**: MongoDB indexes handle uniqueness efficiently

#### Cons

- **Complex Migration**: Requires database schema changes and index creation
- **Error Handling**: Database constraint violations need special handling
- **Flexibility**: Less flexible for complex uniqueness rules (e.g., conditional uniqueness)
- **Performance Impact**: Index maintenance overhead on write operations
