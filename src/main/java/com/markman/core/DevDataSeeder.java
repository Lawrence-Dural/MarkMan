package com.markman.core;

import com.markman.core.organization.Organization;
import com.markman.core.organization.OrganizationRepository;
import com.markman.core.role.RoleRepository;
import com.markman.core.user.User;
import com.markman.core.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates one demo organization and one OWNER login on startup if none
 * exists, so there's something to sign in with locally. Dev profile only —
 * there is no self-service signup yet (that's a later phase).
 */
@Component
@Profile("dev")
class DevDataSeeder implements CommandLineRunner {

    private static final String DEMO_ORG_CODE = "DEMO";
    private static final String DEMO_USERNAME = "owner";
    private static final String DEMO_PASSWORD = "password";

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    DevDataSeeder(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Organization organization = organizationRepository.findByCodeIgnoreCase(DEMO_ORG_CODE)
                .orElseGet(() -> organizationRepository.save(new Organization(DEMO_ORG_CODE, "Demo Cafe")));

        boolean ownerExists = userRepository
                .findByOrganizationIdAndUsernameIgnoreCase(organization.getId(), DEMO_USERNAME)
                .isPresent();

        if (!ownerExists) {
            var ownerRole = roleRepository.findByCodeAndOrganizationIdIsNull("OWNER")
                    .orElseThrow(() -> new IllegalStateException("OWNER system role missing — check V004 migration"));

            userRepository.save(new User(
                    organization.getId(),
                    DEMO_USERNAME,
                    passwordEncoder.encode(DEMO_PASSWORD),
                    ownerRole
            ));

            System.out.println(
                    "Seeded demo login — org code: " + DEMO_ORG_CODE
                            + ", username: " + DEMO_USERNAME
                            + ", password: " + DEMO_PASSWORD
            );
        }
    }
}
