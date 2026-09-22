package com.markman.core.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A permission code, e.g. {@code SALE_VOID}. Global, not tenant-scoped —
 * the same catalog applies to every organization. Seeded by
 * V003__core_permission.sql; not created or edited through the app in V1.
 */
@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @Column(length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String description;

    protected Permission() {
        // JPA
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
