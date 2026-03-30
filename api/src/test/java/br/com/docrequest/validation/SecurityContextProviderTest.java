package br.com.docrequest.validation;

import br.com.docrequest.security.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityContextProviderTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private SecurityContextProvider securityContextProvider;

    @BeforeEach
    void setUp() {
        securityContextProvider = new SecurityContextProvider();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetSecurityContextWithAuthenticatedUser() {
        // Setup
        String userId = "testUser";
        String tenantId = "testTenant";
        String role = "ROLE_USER";
        
        when(authentication.getName()).thenReturn(userId);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn(
            (Collection) Arrays.asList(new SimpleGrantedAuthority(role))
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        // Set tenant context
        TenantContext.setCurrentTenant(tenantId);

        // Execute
        Map<String, Object> result = securityContextProvider.getSecurityContext();

        // Verify
        assertNotNull(result);
        assertEquals(userId, result.get("userId"));
        assertEquals(Arrays.asList(new SimpleGrantedAuthority(role)), result.get("authorities"));
        assertEquals(true, result.get("authenticated"));
        assertEquals(tenantId, result.get("tenantId"));
        
        verify(securityContext).getAuthentication();
        verify(authentication).getName();
        verify(authentication).isAuthenticated();
        verify(authentication).getAuthorities();
    }

    @Test
    void testGetSecurityContextWithUnauthenticatedUser() {
        // Setup
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Execute
        Map<String, Object> result = securityContextProvider.getSecurityContext();

        // Verify
        assertNotNull(result);
        assertEquals(false, result.get("authenticated"));
        assertNull(result.get("userId"));
        assertNull(result.get("authorities"));
        
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
    }

    @Test
    void testGetSecurityContextWithNullAuthentication() {
        // Setup
        when(securityContext.getAuthentication()).thenReturn(null);

        // Execute
        Map<String, Object> result = securityContextProvider.getSecurityContext();

        // Verify
        assertNotNull(result);
        assertEquals(false, result.get("authenticated"));
        assertNull(result.get("userId"));
        assertNull(result.get("authorities"));
        
        verify(securityContext).getAuthentication();
    }

    @Test
    void testGetSecurityContextWithException() {
        // Setup
        when(securityContext.getAuthentication()).thenThrow(new RuntimeException("Security error"));

        // Execute
        Map<String, Object> result = securityContextProvider.getSecurityContext();

        // Verify
        assertNotNull(result);
        assertEquals(false, result.get("authenticated"));
        assertNull(result.get("userId"));
        assertNull(result.get("authorities"));
        
        verify(securityContext).getAuthentication();
    }

    @Test
    void testGetSecurityContextWithMultipleRoles() {
        // Setup
        String userId = "adminUser";
        String tenantId = "adminTenant";
        String role1 = "ROLE_ADMIN";
        String role2 = "ROLE_USER";
        
        when(authentication.getName()).thenReturn(userId);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn(
            (Collection) Arrays.asList(
                new SimpleGrantedAuthority(role1),
                new SimpleGrantedAuthority(role2)
            )
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        // Set tenant context
        TenantContext.setCurrentTenant(tenantId);

        // Execute
        Map<String, Object> result = securityContextProvider.getSecurityContext();

        // Verify
        assertNotNull(result);
        assertEquals(userId, result.get("userId"));
        assertEquals(
            Arrays.asList(
                new SimpleGrantedAuthority(role1),
                new SimpleGrantedAuthority(role2)
            ),
            result.get("authorities")
        );
        assertEquals(true, result.get("authenticated"));
        assertEquals(tenantId, result.get("tenantId"));
    }

    @Test
    void testGetSecurityContextWithEmptyTenant() {
        // Setup
        String userId = "testUser";
        String tenantId = "";
        
        when(authentication.getName()).thenReturn(userId);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        // Set empty tenant context
        TenantContext.setCurrentTenant(tenantId);

        // Execute
        Map<String, Object> result = securityContextProvider.getSecurityContext();

        // Verify
        assertNotNull(result);
        assertEquals(userId, result.get("userId"));
        assertEquals(tenantId, result.get("tenantId"));
    }
}