package com.markman.core.security;

import com.markman.core.organization.Organization;
import com.markman.core.organization.OrganizationRepository;
import com.markman.core.user.User;
import com.markman.core.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class MarkManUserDetailsService implements UserDetailsService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    MarkManUserDetailsService(OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    /**
     * @param combined the org code and username joined by
     *                 {@link LoginIdentifier#encode}, as produced by
     *                 {@link OrgAwareAuthenticationFilter}.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String combined) {
        String[] parts = LoginIdentifier.decode(combined);
        if (parts == null) {
            throw new UsernameNotFoundException("Malformed login identifier");
        }
        String orgCode = parts[0];
        String username = parts[1];

        Organization organization = organizationRepository.findByCodeIgnoreCase(orgCode)
                .orElseThrow(() -> new UsernameNotFoundException("No such organization or user"));

        User user = userRepository.findByOrganizationIdAndUsernameIgnoreCase(organization.getId(), username)
                .orElseThrow(() -> new UsernameNotFoundException("No such organization or user"));

        boolean enabled = user.isActive() && organization.isActive();

        return new MarkManPrincipal(
                user.getId(),
                organization.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                enabled,
                user.getLockedUntil(),
                user.getRole().getPermissionCodes()
        );
    }
}
