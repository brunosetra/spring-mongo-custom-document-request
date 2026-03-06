package br.com.docrequest.domain.enums;

/**
 * Comparison operators for filtering field values in queries.
 * Each operator is compatible with specific field types.
 */
public enum ComparisonOperator {
    /**
     * Equals. Compatible with all types.
     */
    EQ,
    
    /**
     * Not equals. Compatible with all types.
     */
    NE,
    
    /**
     * Greater than. Compatible with numeric and date types.
     */
    GT,
    
    /**
     * Greater than or equal. Compatible with numeric and date types.
     */
    GTE,
    
    /**
     * Less than. Compatible with numeric and date types.
     */
    LT,
    
    /**
     * Less than or equal. Compatible with numeric and date types.
     */
    LTE,
    
    /**
     * Contains substring. Compatible with string types.
     */
    CONTAINS,
    
    /**
     * Starts with. Compatible with string types.
     */
    STARTS_WITH,
    
    /**
     * Ends with. Compatible with string types.
     */
    ENDS_WITH,
    
    /**
     * Value is in list. Compatible with all types.
     */
    IN,
    
    /**
     * Value is not in list. Compatible with all types.
     */
    NOT_IN,
    
    /**
     * Is null. Compatible with all types.
     */
    IS_NULL,
    
    /**
     * Is not null. Compatible with all types.
     */
    IS_NOT_NULL,
    
    /**
     * Is empty (string or array). Compatible with string and array types.
     */
    IS_EMPTY,
    
    /**
     * Is not empty. Compatible with string and array types.
     */
    IS_NOT_EMPTY,
    
    /**
     * Array size equals. Compatible with array types.
     */
    SIZE_EQ,
    
    /**
     * Array size greater than. Compatible with array types.
     */
    SIZE_GT,
    
    /**
     * Array size less than. Compatible with array types.
     */
    SIZE_LT
}
