package com.markman.core.security;

/**
 * MarkMan logs in with (organization code, username, password) — three
 * fields — but Spring Security's {@code UsernamePasswordAuthenticationFilter}
 * machinery is built around a single username string. Rather than write a
 * fully custom {@code AuthenticationProvider}, we combine the two
 * identifying fields into one string at the filter (see
 * {@link OrgAwareAuthenticationFilter}) and split them back apart in
 * {@link MarkManUserDetailsService}, so the rest of the stack (
 * {@code DaoAuthenticationProvider}, {@code PasswordEncoder}, login-failure
 * events) stays the well-trodden Spring Security path.
 */
final class LoginIdentifier {

    // A character that must never appear in an org code or username.
    private static final String DELIMITER = "\u0000";

    private LoginIdentifier() {
    }

    static String encode(String orgCode, String username) {
        return orgCode + DELIMITER + username;
    }

    /**
     * @return {@code [orgCode, username]}, or {@code null} if the string
     * wasn't produced by {@link #encode}.
     */
    static String[] decode(String combined) {
        String[] parts = combined.split(DELIMITER, 2);
        return parts.length == 2 ? parts : null;
    }
}
