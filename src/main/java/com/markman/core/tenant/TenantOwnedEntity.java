package com.markman.core.tenant;

import com.markman.core.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.TenantId;

import java.util.UUID;

/**
 * Base class for every entity owned by an organization.
 * <p>
 * {@code @TenantId} is Hibernate's own multi-tenancy support: it is
 * populated automatically from {@link HibernateTenantIdentifierResolver}
 * on insert, and every query Hibernate issues for this entity is filtered
 * by it automatically. Application code never sets this field — doing so
 * would defeat the point. This is layer 2 of tenant isolation (see
 * {@link CurrentTenant} for layer 1); the database's composite foreign
 * keys are layer 3.
 */
@MappedSuperclass
public abstract class TenantOwnedEntity extends AbstractEntity {

    @TenantId
    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    public UUID getOrganizationId() {
        return organizationId;
    }
}
