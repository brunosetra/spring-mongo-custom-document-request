package br.com.docrequest.util;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.exception.ConversionException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for converting date fields between template format and ISO format.
 * 
 * Flow:
 * - Input (CREATE): Template format (e.g., "dd/MM/yyyy") → ISO format for MongoDB
 * - Output (READ): ISO format from MongoDB → Template format for response
 */
@Slf4j
public final class DateFieldConverter {

    private static final Set<DocRequestFieldType> DATE_TYPES = Set.of(
        DocRequestFieldType.DATE,
        DocRequestFieldType.DATETIME,
        DocRequestFieldType.EXPIRATION_DATE
    );

    private DateFieldConverter() {
        // Utility class
    }

    /**
     * Converts date fields from template format to ISO format for MongoDB storage.
     * 
     * @param fields The input fields map with dates in template format
     * @param metadata The DocRequestMetadata containing field format definitions
     * @return New map with date fields converted to ISO format
     * @throws ConversionException when date conversion fails
     */
    public static Map<String, Object> convertToIsoFormat(
            Map<String, Object> fields, 
            DocRequestMetadata metadata) throws ConversionException {
        
        if (fields == null || fields.isEmpty() || metadata == null) {
            return fields;
        }

        Map<String, Object> convertedFields = new HashMap<>(fields);
        Map<String, FieldFormatInfo> dateFields = collectDateFields(metadata);

        for (Map.Entry<String, FieldFormatInfo> entry : dateFields.entrySet()) {
            String fieldName = entry.getKey();
            FieldFormatInfo formatInfo = entry.getValue();
            Object value = convertedFields.get(fieldName);

            if (value != null && value instanceof String) {
                String isoValue = convertToIso((String) value, formatInfo);
                convertedFields.put(fieldName, isoValue);
                log.debug("Converted field '{}' from '{}' to ISO format '{}'", 
                    fieldName, value, isoValue);
            }
        }

        return convertedFields;
    }

    /**
     * Converts date fields from ISO format to template format for response.
     * 
     * @param fields The fields map with dates in ISO format from MongoDB
     * @param metadata The DocRequestMetadata containing field format definitions
     * @return New map with date fields converted to template format
     * @throws ConversionException when date conversion fails
     */
    public static Map<String, Object> convertToTemplateFormat(
            Map<String, Object> fields, 
            DocRequestMetadata metadata) throws ConversionException {
        
        if (fields == null || fields.isEmpty() || metadata == null) {
            return fields;
        }

        Map<String, Object> convertedFields = new HashMap<>(fields);
        Map<String, FieldFormatInfo> dateFields = collectDateFields(metadata);

        for (Map.Entry<String, FieldFormatInfo> entry : dateFields.entrySet()) {
            String fieldName = entry.getKey();
            FieldFormatInfo formatInfo = entry.getValue();
            Object value = convertedFields.get(fieldName);

            if (value != null) {
                String templateValue = convertToTemplate(value, formatInfo);
                convertedFields.put(fieldName, templateValue);
                log.debug("Converted field '{}' from '{}' to template format '{}'", 
                    fieldName, value, templateValue);
            }
        }

        return convertedFields;
    }

    /**
     * Collects date field information from metadata.
     */
    private static Map<String, FieldFormatInfo> collectDateFields(DocRequestMetadata metadata) {
        Map<String, FieldFormatInfo> dateFields = new HashMap<>();
        
        for (DocRequestFieldMetadata field : metadata.getFields()) {
            if (DATE_TYPES.contains(field.getType()) && field.getFormat() != null) {
                dateFields.put(field.getName(), new FieldFormatInfo(
                    field.getType(),
                    field.getFormat()
                ));
            }
        }
        
        return dateFields;
    }

    /**
     * Converts a date string from template format to ISO format.
     */
    private static String convertToIso(String value, FieldFormatInfo formatInfo) {
        try {
            DateTimeFormatter templateFormatter = DateTimeFormatter.ofPattern(formatInfo.format());
            
            if (formatInfo.fieldType() == DocRequestFieldType.DATE) {
                LocalDate date = LocalDate.parse(value, templateFormatter);
                return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            } else if (formatInfo.fieldType() == DocRequestFieldType.EXPIRATION_DATE) {
                LocalDate date = LocalDate.parse(value, templateFormatter);
                return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            } else {
                // DATETIME
                LocalDateTime dateTime = LocalDateTime.parse(value, templateFormatter);
                return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        } catch (DateTimeParseException e) {
            log.error("Failed to convert date '{}' with format '{}': {}", 
                value, formatInfo.format(), e.getMessage());
            throw new ConversionException("field", value, formatInfo.format(), 
                formatInfo.fieldType() == DocRequestFieldType.DATE ? "ISO_LOCAL_DATE" : 
                formatInfo.fieldType() == DocRequestFieldType.EXPIRATION_DATE ? "ISO_LOCAL_DATE" : "ISO_LOCAL_DATE_TIME",
                e.getMessage(), e);
        }
    }

    /**
     * Converts a date value from ISO format to template format.
     */
    private static String convertToTemplate(Object value, FieldFormatInfo formatInfo) {
        try {
            DateTimeFormatter templateFormatter = DateTimeFormatter.ofPattern(formatInfo.format());

            if (formatInfo.fieldType() == DocRequestFieldType.DATE) {
                LocalDate date;
                if (value instanceof String) {
                    date = LocalDate.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE);
                } else {
                    throw new ConversionException("field", value.toString(), 
                        value.getClass().getSimpleName(), formatInfo.format(),
                        "Invalid value type for DATE field, expected String");
                }
                return date.format(templateFormatter);
            } else if (formatInfo.fieldType() == DocRequestFieldType.EXPIRATION_DATE) {
                LocalDate date;
                if (value instanceof String) {
                    date = LocalDate.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE);
                } else {
                    throw new ConversionException("field", value.toString(), 
                        value.getClass().getSimpleName(), formatInfo.format(),
                        "Invalid value type for EXPIRATION_DATE field, expected String");
                }
                return date.format(templateFormatter);
            } else {
                // DATETIME
                LocalDateTime dateTime;
                if (value instanceof String) {
                    dateTime = LocalDateTime.parse((String) value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } else {
                    throw new ConversionException("field", value.toString(), 
                        value.getClass().getSimpleName(), formatInfo.format(),
                        "Invalid value type for DATETIME field, expected String");
                }
                return dateTime.format(templateFormatter);
            }
        } catch (DateTimeParseException e) {
            log.error("Failed to convert date '{}' to template format '{}': {}", 
                value, formatInfo.format(), e.getMessage());
            throw new ConversionException("field", value.toString(), 
                formatInfo.fieldType() == DocRequestFieldType.DATE ? "ISO_LOCAL_DATE" : 
                formatInfo.fieldType() == DocRequestFieldType.EXPIRATION_DATE ? "ISO_LOCAL_DATE" : "ISO_LOCAL_DATE_TIME",
                formatInfo.format(), e.getMessage(), e);
        }
    }

    /**
     * Helper record to hold field format information.
     */
    private record FieldFormatInfo(DocRequestFieldType fieldType, String format) {
    }
}
