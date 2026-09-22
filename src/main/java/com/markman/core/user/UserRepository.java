package com.markman.core.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Every lookup here takes {@code organizationId} explicitly and returns
 * empty rather than a cross-tenant row — there is no bare
 * {@code findById}-style method by design. See {@link User}'s Javadoc for
 * why this entity isn't covered by Hibernate's automatic {@code @TenantId}
 * filtering instead.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByOrganizationIdAndId(UUID organizationId, UUID id);

    Optional<User> findByOrganizationIdAndUsernameIgnoreCase(UUID organizationId, String username);
}
