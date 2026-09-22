package com.markman.core.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The authenticated principal for every request. Built once at login from
 * the {@code User} entity and held in the {@code SecurityContext} for the
 * life of the session — never rebuilt from request data.
 */
public final class MarkManPrincipal implements UserDetails {

    private final UUID userId;
    private final UUID organizationId;
    private final String username;
    private final String passwordHash;
    private final boolean enabled;
    private final Instant lockedUntil;
    private final Set<String> permissionCodes;

    public MarkManPrincipal(
            UUID userId,
            UUID organizationId,
            String username,
            String passwordHash,
            boolean enabled,
            Instant lockedUntil,
            Set<String> permissionCodes
    ) {
        this.userId = userId;
        this.organizationId = organizationId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.lockedUntil = lockedUntil;
        this.permissionCodes = Set.copyOf(permissionCodes);
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissionCodes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return lockedUntil == null || lockedUntil.isBefore(Instant.now());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
