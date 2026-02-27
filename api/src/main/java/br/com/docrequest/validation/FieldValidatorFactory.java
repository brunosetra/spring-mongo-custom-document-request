package br.com.docrequest.validation;

import br.com.docrequest.domain.enums.DocRequestFieldType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Factory that resolves the appropriate FieldValidator for a given DocRequestFieldType.
 * Uses Spring's dependency injection to collect all FieldValidator implementations.
 * Implements the Factory Pattern.
 */
@Component
public class FieldValidatorFactory {

    private final Map<DocRequestFieldType, FieldValidator> validatorMap;

    public FieldValidatorFactory(List<FieldValidator> validators) {
        this.validatorMap = new EnumMap<>(DocRequestFieldType.class);
        for (FieldValidator validator : validators) {
            validatorMap.put(validator.getType(), validator);
        }
    }

    /**
     * Returns the validator for the given field type.
     *
     * @param type the field type
     * @return the corresponding FieldValidator
     * @throws IllegalArgumentException if no validator is registered for the type
     */
    public FieldValidator getValidator(DocRequestFieldType type) {
        FieldValidator validator = validatorMap.get(type);
        if (validator == null) {
            throw new IllegalArgumentException("No validator registered for field type: " + type);
        }
        return validator;
    }

    /**
     * Checks if a validator exists for the given type.
     */
    public boolean hasValidator(DocRequestFieldType type) {
        return validatorMap.containsKey(type);
    }
}
