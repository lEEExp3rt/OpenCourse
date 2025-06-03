package org.opencourse.utils.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.opencourse.models.User;
import org.opencourse.repositories.UserRepo;
import org.opencourse.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authorization Filter to validate JWT tokens and set the user authentication.
 * 
 * @author LJX
 * @author !EEExp3rt
 * @apiNote This filter checks the authorization before a request is processed in controller layer.
 */
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepo userRepo;

    @Autowired
    public JwtAuthorizationFilter(JwtUtils jwtUtils, UserRepo userRepo) {
        this.jwtUtils = jwtUtils;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        // Get the Authorization header from the request.
        final String authorizationHeader = request.getHeader("Authorization");

        String jwt = null;

        // Check if the Authorization header is present and starts with "Bearer ".
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            // Load user details and set authentication in the security context.
            try {
                User user = validateToken(jwt);
                UserAuthentication authentication = new UserAuthentication(
                    user, new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                throw e;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Validates the JWT token and return the user.
     * 
     * @param jwt The JWT token to validate.
     * @return The user if the token is valid, otherwise null.
     * @throws RuntimeException If the token is invalid or expired or the user is not found.
     */
    private User validateToken(String jwt) throws RuntimeException{
        if (jwt == null || jwt.isEmpty()) {
            throw new RuntimeException("Invalid JWT token");
        }
        if (jwtUtils.isTokenExpired(jwt)) {
            throw new RuntimeException("JWT token is expired");
        }
        User user = userRepo.findByEmail(jwtUtils.getUsernameFromToken(jwt))
            .orElseThrow(() -> new RuntimeException("User not found in JWT authorization"));
        return user;
    }
}
