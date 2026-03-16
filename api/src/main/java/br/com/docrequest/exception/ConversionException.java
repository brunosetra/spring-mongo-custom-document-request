package br.com.docrequest.exception;

/**
 * Exception thrown when date field conversion fails between template format and ISO format.
 */
public class ConversionException extends RuntimeException {

    private final String fieldName;
    private final String inputValue;
    private final String targetFormat;
    private final String sourceFormat;

    public ConversionException(String fieldName, String inputValue, String sourceFormat, String targetFormat, String message) {
        super(String.format("Failed to convert field '%s' from '%s' (format: %s) to '%s' (format: %s): %s", 
            fieldName, inputValue, sourceFormat, targetFormat, targetFormat, message));
        this.fieldName = fieldName;
        this.inputValue = inputValue;
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
    }

    public ConversionException(String fieldName, String inputValue, String sourceFormat, String targetFormat, String message, Throwable cause) {
        super(String.format("Failed to convert field '%s' from '%s' (format: %s) to '%s' (format: %s): %s", 
            fieldName, inputValue, sourceFormat, targetFormat, targetFormat, message), cause);
        this.fieldName = fieldName;
        this.inputValue = inputValue;
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getInputValue() {
        return inputValue;
    }

    public String getSourceFormat() {
        return sourceFormat;
    }

    public String getTargetFormat() {
        return targetFormat;
    }
}