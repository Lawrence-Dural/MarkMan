package com.markman.core.role;

import com.markman.core.AbstractEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.Set;
import java.util.UUID;

/**
 * A role: a named bundle of permission codes. {@code organizationId} is
 * null for the five system roles (OWNER, ADMIN, MANAGER, CASHIER, STAFF),
 * shared by every organization and seeded by V004__core_role.sql. A
 * non-null organizationId is room for custom, per-organization roles
 * later — not used or created anywhere in V1.
 * <p>
 * Deliberately does NOT extend {@code TenantOwnedEntity}: a system role has
 * no organization at all, and even a future custom role must be readable
 * before its owning organization is the authenticated tenant (same
 * bootstrapping concern as {@code User} — see its Javadoc).
 */
@Entity
@Table(name = "role")
public class Role extends AbstractEntity {

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission_code")
    private Set<String> permissionCodes = Set.of();

    protected Role() {
        // JPA
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isSystemRole() {
        return organizationId == null;
    }

    public Set<String> getPermissionCodes() {
        return Set.copyOf(permissionCodes);
    }
}
