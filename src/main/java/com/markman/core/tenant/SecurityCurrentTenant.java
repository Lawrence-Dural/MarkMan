package com.markman.core.tenant;

import com.markman.core.security.MarkManPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class SecurityCurrentTenant implements CurrentTenant {

    @Override
    public UUID require() {
        return currentOrNone().orElseThrow(
                () -> new IllegalStateException("No authenticated tenant in the current context")
        );
    }

    @Override
    public Optional<UUID> currentOrNone() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        if (!(authentication.getPrincipal() instanceof MarkManPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal.getOrganizationId());
    }
}
