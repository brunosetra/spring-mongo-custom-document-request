package br.com.docrequest.validation;

import br.com.docrequest.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityContextProvider {

    public Map<String, Object> getSecurityContext() {
        Map<String, Object> context = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                context.put("userId", authentication.getName());
                context.put("authorities", authentication.getAuthorities());
                context.put("authenticated", true);
            } else {
                context.put("authenticated", false);
            }
        } catch (Exception e) {
            log.warn("Error retrieving security context: {}", e.getMessage());
            context.put("authenticated", false);
        }
        
        context.put("tenantId", TenantContext.getCurrentTenant());
        return context;
    }
}