package com.markman.core.user;

import com.markman.core.AbstractEntity;
import com.markman.core.role.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

/**
 * A staff login. One identity for both "user" and "employee" — MarkMan
 * does not have a separate Employee table in V1.
 * <p>
 * Deliberately does NOT extend {@code TenantOwnedEntity}: login happens
 * <em>before</em> there is an authenticated tenant for Hibernate's
 * {@code @TenantId} filter to resolve (that's exactly what logging in
 * establishes), so this entity cannot depend on it. Tenant isolation for
 * User is instead enforced explicitly — every lookup goes through
 * {@link UserRepository}'s organization-scoped methods (layer 1) plus the
 * database's NOT NULL organization_id and composite foreign keys
 * (layer 3). See ARCHITECTURE.md's tenant isolation section for the full
 * four-layer model.
 */
@Entity
@Table(name = "`user`")
public class User extends AbstractEntity {

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Version
    private long version;

    protected User() {
        // JPA
    }

    public User(UUID organizationId, String username, String passwordHash, Role role) {
        this.organizationId = organizationId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.failedLoginCount = 0;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public int getFailedLoginCount() {
        return failedLoginCount;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(Instant.now());
    }

    /**
     * Called on a failed login attempt. Locks the account for 15 minutes
     * after 5 consecutive failures; a successful login resets the count.
     */
    public void recordFailedLogin() {
        this.failedLoginCount++;
        if (this.failedLoginCount >= 5) {
            this.lockedUntil = Instant.now().plusSeconds(15 * 60);
        }
    }

    public void recordSuccessfulLogin() {
        this.failedLoginCount = 0;
        this.lockedUntil = null;
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
    }

    public void reactivate() {
        this.status = UserStatus.ACTIVE;
    }
}
