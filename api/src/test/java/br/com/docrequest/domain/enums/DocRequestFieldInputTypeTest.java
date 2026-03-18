package br.com.docrequest.domain.enums;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DocRequestFieldInputType enum.
 * Tests the new AUTO_INC enum value and overall enum functionality.
 */
class DocRequestFieldInputTypeTest {

    @Test
    void testAutoIncEnumValueExists() {
        // Test that AUTO_INC enum value exists
        DocRequestFieldInputType autoInc = DocRequestFieldInputType.valueOf("AUTO_INC");
        assertNotNull(autoInc, "AUTO_INC enum value should exist");
        assertEquals(DocRequestFieldInputType.AUTO_INC, autoInc, "AUTO_INC should be accessible");
    }

    @Test
    void testAutoIncDescription() {
        // Test that AUTO_INC has the correct description
        DocRequestFieldInputType autoInc = DocRequestFieldInputType.AUTO_INC;
        assertEquals("Auto Incremento", autoInc.description, "AUTO_INC should have correct description");
    }

    @Test
    void testAllEnumValues() {
        // Test all enum values are properly defined
        DocRequestFieldInputType[] values = DocRequestFieldInputType.values();
        
        // Verify we have the expected number of values (original 7 + 1 AUTO_INC)
        assertEquals(8, values.length, "Should have 8 enum values");
        
        // Verify all expected values exist
        assertTrue(Arrays.stream(values).anyMatch(v -> v == DocRequestFieldInputType.IN), "IN should exist");
        assertTrue(Arrays.stream(values).anyMatch(v -> v == DocRequestFieldInputType.FIXED), "FIXED should exist");
        assertTrue(Arrays.stream(values).anyMatch(v -> v == DocRequestFieldInputType.CALCULATED), "CALCULATED should exist");
        assertTrue(Arrays.stream(values).anyMatch(v -> v == DocRequestFieldInputType.DEFAULT), "DEFAULT should exist");
        assertTrue(Arrays.stream(values).anyMatch(v -> v == DocRequestFieldInputType.DOMAIN), "DOMAIN should exist");
        assertTrue(Arrays.stream(values).anyMatch(v -> v == DocRequestFieldInputType.DOMAIN_CALCULATED), "DOMAIN_CALCULATED should exist");
        assertTrue(Arrays.stream(values).anyMatch(v -> v == DocRequestFieldInputType.INTERNAL), "INTERNAL should exist");
        assertTrue(Arrays.stream(values).anyMatch(v -> v == DocRequestFieldInputType.AUTO_INC), "AUTO_INC should exist");
    }

    @Test
    void testEnumValueDescriptions() {
        // Test that all enum values have non-null descriptions
        for (DocRequestFieldInputType type : DocRequestFieldInputType.values()) {
            assertNotNull(type.description, "All enum values should have descriptions");
            assertFalse(type.description.isBlank(), "All enum descriptions should not be blank");
        }
    }

    @Test
    void testEnumValueUniqueness() {
        // Test that all enum values are unique
        DocRequestFieldInputType[] values = DocRequestFieldInputType.values();
        Set<DocRequestFieldInputType> uniqueValues = new HashSet<>(Arrays.asList(values));
        assertEquals(values.length, uniqueValues.size(), "All enum values should be unique");
    }

    @Test
    void testAutoIncIsDistinct() {
        // Test that AUTO_INC is distinct from other values
        assertNotEquals(DocRequestFieldInputType.IN, DocRequestFieldInputType.AUTO_INC, "AUTO_INC should be different from IN");
        assertNotEquals(DocRequestFieldInputType.FIXED, DocRequestFieldInputType.AUTO_INC, "AUTO_INC should be different from FIXED");
        assertNotEquals(DocRequestFieldInputType.CALCULATED, DocRequestFieldInputType.AUTO_INC, "AUTO_INC should be different from CALCULATED");
        assertNotEquals(DocRequestFieldInputType.DEFAULT, DocRequestFieldInputType.AUTO_INC, "AUTO_INC should be different from DEFAULT");
        assertNotEquals(DocRequestFieldInputType.DOMAIN, DocRequestFieldInputType.AUTO_INC, "AUTO_INC should be different from DOMAIN");
        assertNotEquals(DocRequestFieldInputType.DOMAIN_CALCULATED, DocRequestFieldInputType.AUTO_INC, "AUTO_INC should be different from DOMAIN_CALCULATED");
        assertNotEquals(DocRequestFieldInputType.INTERNAL, DocRequestFieldInputType.AUTO_INC, "AUTO_INC should be different from INTERNAL");
    }

    @Test
    void testEnumToString() {
        // Test that enum toString() returns the enum name
        assertEquals("AUTO_INC", DocRequestFieldInputType.AUTO_INC.toString(), "toString() should return enum name");
    }

    @Test
    void testEnumValueOf() {
        // Test that valueOf() works correctly
        DocRequestFieldInputType autoInc = DocRequestFieldInputType.valueOf("AUTO_INC");
        assertEquals(DocRequestFieldInputType.AUTO_INC, autoInc, "valueOf() should return correct enum value");
    }
}