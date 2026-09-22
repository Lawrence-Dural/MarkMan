package com.markman.core.tenant;

import java.util.Optional;
import java.util.UUID;

/**
 * The single source of truth for "which organization is this request for".
 * <p>
 * Every tenant-scoped lookup in the codebase must go through this — never
 * through an organizationId read from a request, URL, form or DTO. See
 * {@code SecurityCurrentTenant} for the implementation, which reads
 * exclusively from the authenticated {@code SecurityContext}.
 */
public interface CurrentTenant {

    /**
     * The organization of the currently authenticated user.
     *
     * @throws IllegalStateException if there is no authenticated tenant
     *                                context (e.g. an anonymous request)
     */
    UUID require();

    /**
     * Same as {@link #require()} but without throwing — for code paths
     * (like the tenant identifier resolver itself) that must tolerate
     * there being no authenticated user yet.
     */
    Optional<UUID> currentOrNone();
}
