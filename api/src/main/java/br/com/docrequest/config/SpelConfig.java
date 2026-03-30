package br.com.docrequest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Configuration class for Spring Expression Language (SpEL) beans.
 * This configuration ensures that SpEL-related beans are properly defined and available for injection.
 */
@Configuration
public class SpelConfig {

    /**
     * Creates and configures a SpEL Expression Parser bean.
     * This parser is used to evaluate SpEL expressions in the validation framework.
     *
     * @return configured SpelExpressionParser instance
     */
    @Bean
    public SpelExpressionParser spelExpressionParser() {
        return new SpelExpressionParser();
    }
}