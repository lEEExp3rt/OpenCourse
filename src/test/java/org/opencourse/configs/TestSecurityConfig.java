package org.opencourse.configs;

import org.opencourse.repositories.UserRepo;
import org.opencourse.utils.JwtUtils;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;

/**
 * Test security configuration to disable security for unit tests.
 * 
 * @author Lee X ALEX
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtUtils jwtUtils() {
        JwtUtils jwtUtils = new JwtUtils();
        // Set test values using reflection to avoid property placeholder issues
        ReflectionTestUtils.setField(jwtUtils, "secret",
                "test-jwt-secret-key-for-unit-tests-should-be-at-least-32-characters");
        ReflectionTestUtils.setField(jwtUtils, "expirationTime", 86400000L);
        return jwtUtils;
    }

    @Bean
    @Primary
    public UserRepo userRepo() {
        return mock(UserRepo.class);
    }

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
