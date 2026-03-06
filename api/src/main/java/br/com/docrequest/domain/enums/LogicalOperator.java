package br.com.docrequest.domain.enums;

/**
 * Logical operators for combining filter conditions in queries.
 * Used to create complex nested query logic.
 */
public enum LogicalOperator {
    /**
     * All conditions must be true.
     */
    AND,
    
    /**
     * At least one condition must be true.
     */
    OR,
    
    /**
     * Negates the condition(s).
     */
    NOT
}
