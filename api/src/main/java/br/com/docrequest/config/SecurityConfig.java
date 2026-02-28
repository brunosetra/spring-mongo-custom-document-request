package br.com.docrequest.config;

import br.com.docrequest.security.TenantContextFilter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final TenantContextFilter tenantContextFilter;

    @Value("${app.security.roles-claim:realm_access.roles}")
    private String rolesClaim;

    public SecurityConfig(TenantContextFilter tenantContextFilter) {
        this.tenantContextFilter = tenantContextFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Actuator and OpenAPI - public
                .requestMatchers("/actuator/health", "/actuator/info")
                    .permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                // All other requests require authentication
                .anyRequest()
                    .authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            // place tenant filter after JWT has been processed (BearerTokenAuthenticationFilter)
        .addFilterAfter(tenantContextFilter, org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Extract roles based on configurable claim path (e.g., "roles" or "realm_access.roles")
            Collection<String> roles = extractRoleByClaim(jwt, rolesClaim);
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .collect(Collectors.toList());
        });
        return converter;
    }

    /**
     * Extract roles from JWT using configurable claim path.
     * Supports both nested (e.g., "realm_access.roles") and root level (e.g., "roles") claims.
     */
    @SuppressWarnings("unchecked")
    private Collection<String> extractRoleByClaim(org.springframework.security.oauth2.jwt.Jwt jwt, String claimPath) {
        if (claimPath == null || claimPath.isBlank()) {
            return List.of();
        }

        // Handle nested claims (e.g., "realm_access.roles")
        if (claimPath.contains(".")) {
            String[] parts = claimPath.split("\\.", 2);
            Map<String, Object> nested = jwt.getClaimAsMap(parts[0]);
            if (nested != null && nested.containsKey(parts[1])) {
                Object rolesObj = nested.get(parts[1]);
                if (rolesObj instanceof Collection) {
                    return (Collection<String>) rolesObj;
                }
            }
        } else {
            // Handle root level claims (e.g., "roles")
            Object rolesObj = jwt.getClaim(claimPath);
            if (rolesObj instanceof Collection) {
                return (Collection<String>) rolesObj;
            }
        }
        return List.of();
    }
}
