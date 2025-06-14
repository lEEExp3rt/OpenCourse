package org.opencourse.utils.security;

import org.opencourse.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Security utility to load {@link User} from the security context.
 * 
 * @author !EEExp3rt
 */
@Component
public class SecurityUtils {

    /**
     * Get the current authenticated user from the security context.
     * 
     * @return The current authenticated user.
     * @throws RuntimeException If the user is not authenticated or cannot be retrieved.
     */
    public static User getCurrentUser() throws RuntimeException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("用户未认证");
        }
        Object userObj = auth.getPrincipal();
        if (userObj instanceof User) {
            return (User) userObj;
        }
        throw new RuntimeException("无法获取当前用户信息");
    }
}
