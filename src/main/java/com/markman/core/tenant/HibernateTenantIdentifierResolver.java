package com.markman.core.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Feeds {@link CurrentTenant} into Hibernate's own multi-tenancy support,
 * so every {@code @TenantId}-annotated entity is filtered and stamped
 * automatically. Used by entities from Phase 4 onward (catalog, inventory,
 * pos, ...) — not by {@code User}, which must be queryable during login,
 * before any tenant is authenticated. See {@code User}'s Javadoc.
 */
@Component
class HibernateTenantIdentifierResolver implements CurrentTenantIdentifierResolver<UUID> {

    private final CurrentTenant currentTenant;

    HibernateTenantIdentifierResolver(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        return currentTenant.require();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
