package org.opencourse.utils.security;

import java.util.Collection;

import org.opencourse.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * User authentication class in Spring security context.
 * 
 * @author !EEExp3rt
 */
public class UserAuthentication implements Authentication {

    private final User user;    
    private Object details;
    private boolean authenticated;

    /**
     * Constructor.
     * 
     * @param user The user to authenticate.
     * @param details Additional details about the authentication.
     */
    public UserAuthentication(User user, Object details) {
        this.user = user;
        this.details = details;
        this.authenticated = user != null && user.getId() != null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return user.getPassword();
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public User getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }
}
