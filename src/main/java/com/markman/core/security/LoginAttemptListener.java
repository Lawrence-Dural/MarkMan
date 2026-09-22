package com.markman.core.security;

import com.markman.core.organization.OrganizationRepository;
import com.markman.core.user.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Updates {@code User.failedLoginCount}/{@code lockedUntil} from Spring
 * Security's authentication events. Only bad-credentials failures count —
 * an already-disabled or already-locked account doesn't get penalized
 * further just for someone trying it again.
 */
@Component
class LoginAttemptListener {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    LoginAttemptListener(OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    @EventListener
    @Transactional
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        String[] parts = LoginIdentifier.decode(event.getAuthentication().getName());
        if (parts == null) {
            return;
        }
        organizationRepository.findByCodeIgnoreCase(parts[0]).ifPresent(organization ->
                userRepository.findByOrganizationIdAndUsernameIgnoreCase(organization.getId(), parts[1])
                        .ifPresent(user -> {
                            user.recordFailedLogin();
                            userRepository.save(user);
                        })
        );
    }

    @EventListener
    @Transactional
    public void onSuccess(AuthenticationSuccessEvent event) {
        if (!(event.getAuthentication().getPrincipal() instanceof MarkManPrincipal principal)) {
            return;
        }
        userRepository.findByOrganizationIdAndId(principal.getOrganizationId(), principal.getUserId())
                .ifPresent(user -> {
                    user.recordSuccessfulLogin();
                    userRepository.save(user);
                });
    }
}
