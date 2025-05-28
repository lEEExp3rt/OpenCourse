package org.opencourse.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencourse.models.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JWT工具类测试
 */
public class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String SECRET = "testsecretkeymusthaveminimum256bitstestsecretkeymusthaveminimum256bits";
    private final long EXPIRATION = 3600000; // 1小时

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "expirationTime", EXPIRATION);
    }

    @Test
    void testGenerateToken_WithUserDetails() {
        // Arrange
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        // Act
        String token = jwtUtils.generateToken(userDetails);

        // Assert
        assertThat(token).isNotEmpty();
        
        // 验证令牌内容
        String username = jwtUtils.getUsernameFromToken(token);
        assertThat(username).isEqualTo("test@example.com");
        
        // 验证令牌是否有效
        boolean isValid = jwtUtils.validateToken(token, userDetails);
        assertThat(isValid).isTrue();
    }

    @Test
    void testGenerateToken_WithUser() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password", User.UserRole.USER);
        // 模拟用户ID
        ReflectionTestUtils.setField(user, "id", 1);

        // Act
        String token = jwtUtils.generateToken(user);

        // Assert
        assertThat(token).isNotEmpty();
        
        // 验证令牌内容
        String email = jwtUtils.getUsernameFromToken(token);
        assertThat(email).isEqualTo("test@example.com");
        
        // 验证自定义claim
        Claims claims = getAllClaimsFromToken(token);
        assertThat(claims.get("userId")).isEqualTo(1);
        assertThat(claims.get("role")).isEqualTo("USER");
    }

    @Test
    void testGetUsernameFromToken() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password", User.UserRole.USER);
        String token = jwtUtils.generateToken(user);

        // Act
        String username = jwtUtils.getUsernameFromToken(token);

        // Assert
        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    void testGetExpirationDateFromToken() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password", User.UserRole.USER);
        String token = jwtUtils.generateToken(user);

        // Act
        Date expirationDate = jwtUtils.getExpirationDateFromToken(token);

        // Assert
        // 过期时间应该是当前时间加上过期时间
        long currentTime = System.currentTimeMillis();
        long expirationTime = expirationDate.getTime();
        
        // 令牌过期时间应该在当前时间之后
        assertThat(expirationTime).isGreaterThan(currentTime);
        
        // 令牌过期时间应该在创建时间加上过期时间之内（允许1秒误差）
        long expectedExpiration = currentTime + EXPIRATION;
        assertThat(expirationTime).isBetween(expectedExpiration - 1000, expectedExpiration + 1000);
    }
    
    @Test
    void testValidateToken_WithValidToken() {
        // Arrange
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
                
        String token = jwtUtils.generateToken(userDetails);

        // Act
        boolean isValid = jwtUtils.validateToken(token, userDetails);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateToken_WithInvalidUsername() {
        // Arrange
        UserDetails validUser = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
                
        UserDetails invalidUser = org.springframework.security.core.userdetails.User
                .withUsername("wrong@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
                
        String token = jwtUtils.generateToken(validUser);

        // Act
        boolean isValid = jwtUtils.validateToken(token, invalidUser);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateToken_WithExpiredToken() throws Exception {
        // 创建一个带有过期日期的自定义JWT令牌（通过反射直接设置过期时间）
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        
        // 设置一个较长但已经过去的过期时间（-1000毫秒，即1秒前过期）
        ReflectionTestUtils.setField(jwtUtils, "expirationTime", -1000L);
        
        // 生成令牌（这个令牌从创建时就已过期）
        String token = jwtUtils.generateToken(userDetails);
        
        // 重置令牌过期时间为默认值，避免影响其他测试
        ReflectionTestUtils.setField(jwtUtils, "expirationTime", EXPIRATION);
        
        // 验证令牌 - 应该被检测为过期
        boolean isValid = jwtUtils.validateToken(token, userDetails);
        
        // 验证结果
        assertThat(isValid).isFalse();
    }
    
    // 使用反射获取私有方法
    private Claims getAllClaimsFromToken(String token) {
        try {
            Method method = JwtUtils.class.getDeclaredMethod("getAllClaimsFromToken", String.class);
            method.setAccessible(true);
            return (Claims) method.invoke(jwtUtils, token);
        } catch (Exception e) {
            throw new RuntimeException("无法访问getAllClaimsFromToken方法", e);
        }
    }
} 