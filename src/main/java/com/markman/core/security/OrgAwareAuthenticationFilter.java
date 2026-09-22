package com.markman.core.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Reads the login form's {@code orgCode} and {@code username} fields and
 * combines them into the single identifier {@link MarkManUserDetailsService}
 * expects. See {@link LoginIdentifier} for why.
 */
public class OrgAwareAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String orgCode = request.getParameter("orgCode");
        String username = super.obtainUsername(request); // reads the "username" parameter
        if (orgCode == null || username == null) {
            return "";
        }
        return LoginIdentifier.encode(orgCode.strip(), username.strip());
    }
}
