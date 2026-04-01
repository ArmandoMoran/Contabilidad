package com.contabilidad.shared;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

public final class SecurityContextUtils {

    private SecurityContextUtils() {}

    public static AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("Authentication is required");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser user) {
            return user;
        }

        throw new InsufficientAuthenticationException("Authenticated user context is invalid");
    }

    public static UUID currentCompanyId() {
        return currentUser().companyId();
    }

    public static UUID currentUserId() {
        return currentUser().id();
    }
}
