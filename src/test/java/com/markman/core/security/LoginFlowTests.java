package com.markman.core.security;

import com.markman.core.organization.Organization;
import com.markman.core.organization.OrganizationRepository;
import com.markman.core.role.Role;
import com.markman.core.role.RoleRepository;
import com.markman.core.user.User;
import com.markman.core.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class LoginFlowTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:18"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Organization organization;

    @BeforeEach
    void seedOrgAndUser() {
        organization = organizationRepository.save(new Organization("LOGINTEST" + System.nanoTime(), "Login Test Org"));
        Role cashierRole = roleRepository.findByCodeAndOrganizationIdIsNull("CASHIER").orElseThrow();
        userRepository.save(new User(
                organization.getId(), "cashier1", passwordEncoder.encode("correct-horse"), cashierRole
        ));
    }

    private static MockHttpServletRequestBuilder loginRequest(String orgCode, String username, String password) {
        return post("/login")
                .param("orgCode", orgCode)
                .param("username", username)
                .param("password", password)
                .with(csrf());
    }

    @Test
    void correctCredentialsLogIn() throws Exception {
        mockMvc.perform(loginRequest(organization.getCode(), "cashier1", "correct-horse"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void wrongPasswordIsRejectedAndCounted() throws Exception {
        mockMvc.perform(loginRequest(organization.getCode(), "cashier1", "wrong-password"))
                .andExpect(redirectedUrl("/login?error"));

        User user = userRepository
                .findByOrganizationIdAndUsernameIgnoreCase(organization.getId(), "cashier1")
                .orElseThrow();
        assertThat(user.getFailedLoginCount()).isEqualTo(1);
    }

    @Test
    void accountLocksAfterFiveFailures() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(loginRequest(organization.getCode(), "cashier1", "wrong-password"));
        }

        User user = userRepository
                .findByOrganizationIdAndUsernameIgnoreCase(organization.getId(), "cashier1")
                .orElseThrow();
        assertThat(user.isLocked()).isTrue();

        // Even the correct password is now rejected while locked.
        mockMvc.perform(loginRequest(organization.getCode(), "cashier1", "correct-horse"))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void wrongOrganizationCodeIsRejected() throws Exception {
        mockMvc.perform(loginRequest("NO-SUCH-ORG", "cashier1", "correct-horse"))
                .andExpect(redirectedUrl("/login?error"));
    }
}
