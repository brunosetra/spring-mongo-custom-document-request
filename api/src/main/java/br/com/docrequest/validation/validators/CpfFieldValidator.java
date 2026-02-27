package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates Brazilian CPF numbers.
 * Accepts formats: 000.000.000-00 or 00000000000
 */
@Component
public class CpfFieldValidator extends AbstractFieldValidator {

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.CPF;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        String cpf = value.toString().trim().replaceAll("[.\\-]", "");

        if (!isValidCpf(cpf)) {
            return Optional.of(buildError(fieldName, metadata, "ERR_CPF_INVALID",
                "Field '" + fieldName + "' must be a valid CPF", value));
        }

        return Optional.empty();
    }

    private boolean isValidCpf(String cpf) {
        if (cpf.length() != 11 || !cpf.matches("\\d{11}")) {
            return false;
        }

        // Check for all-same-digit CPFs (e.g., 111.111.111-11)
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        // Validate first check digit
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;
        if (firstDigit != (cpf.charAt(9) - '0')) {
            return false;
        }

        // Validate second check digit
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;
        return secondDigit == (cpf.charAt(10) - '0');
    }
}
