package com.markman.core;

import com.markman.core.organization.Organization;
import com.markman.core.organization.OrganizationRepository;
import com.markman.core.role.Role;
import com.markman.core.role.RoleRepository;
import com.markman.core.user.User;
import com.markman.core.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves the layer-1 (application) part of tenant isolation for
 * {@code User}: given a real user belonging to organization A, no lookup
 * scoped to organization B's id can ever return that user, no matter which
 * id is passed. This is the pattern every future org-scoped entity's
 * repository tests should follow — two real organizations, cross the ids,
 * expect empty rather than 403.
 */
@SpringBootTest
@Testcontainers
class TenantIsolationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:18"));

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Organization orgA;
    private Organization orgB;
    private User userInOrgA;

    @BeforeEach
    void seedTwoOrganizations() {
        orgA = organizationRepository.save(new Organization("ORGA" + System.nanoTime(), "Org A"));
        orgB = organizationRepository.save(new Organization("ORGB" + System.nanoTime(), "Org B"));

        Role cashierRole = roleRepository.findByCodeAndOrganizationIdIsNull("CASHIER").orElseThrow();

        userInOrgA = userRepository.save(new User(orgA.getId(), "cashier1", "{noop}irrelevant", cashierRole));
    }

    @Test
    void userFromOrgAIsNotVisibleThroughOrgBsId() {
        Optional<User> result = userRepository.findByOrganizationIdAndId(orgB.getId(), userInOrgA.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void userFromOrgAIsVisibleThroughOrgAsOwnId() {
        Optional<User> result = userRepository.findByOrganizationIdAndId(orgA.getId(), userInOrgA.getId());

        assertThat(result).isPresent();
    }

    @Test
    void sameUsernameCanExistInDifferentOrganizations() {
        Role cashierRole = roleRepository.findByCodeAndOrganizationIdIsNull("CASHIER").orElseThrow();

        // Same username "cashier1" as userInOrgA, but a different organization.
        User userInOrgB = userRepository.save(new User(orgB.getId(), "cashier1", "{noop}irrelevant", cashierRole));

        assertThat(userInOrgB.getId()).isNotEqualTo(userInOrgA.getId());
        assertThat(userRepository.findByOrganizationIdAndUsernameIgnoreCase(orgA.getId(), "cashier1"))
                .contains(userInOrgA);
        assertThat(userRepository.findByOrganizationIdAndUsernameIgnoreCase(orgB.getId(), "cashier1"))
                .contains(userInOrgB);
    }
}
