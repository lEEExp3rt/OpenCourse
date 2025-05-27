package org.opencourse.services.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 验证码服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private VerificationService verificationService;

    private final long EXPIRATION_SECONDS = 300; // 5分钟

    @BeforeEach
    void setUp() {
        verificationService = new VerificationService(redisTemplate);
        ReflectionTestUtils.setField(verificationService, "codeExpirationSeconds", EXPIRATION_SECONDS);
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testSaveVerificationCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        String expectedKey = "verification:code:" + email;
        
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // Act
        verificationService.saveVerificationCode(email, code);

        // Assert
        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(
            eq(expectedKey), 
            eq(code), 
            eq(EXPIRATION_SECONDS), 
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void testGetVerificationCode() {
        // Arrange
        String email = "test@example.com";
        String expectedCode = "123456";
        String expectedKey = "verification:code:" + email;
        
        when(valueOperations.get(expectedKey)).thenReturn(expectedCode);

        // Act
        String result = verificationService.getVerificationCode(email);

        // Assert
        assertThat(result).isEqualTo(expectedCode);
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(expectedKey);
    }

    @Test
    void testGetVerificationCode_WhenNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String expectedKey = "verification:code:" + email;
        
        when(valueOperations.get(expectedKey)).thenReturn(null);

        // Act
        String result = verificationService.getVerificationCode(email);

        // Assert
        assertThat(result).isNull();
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(expectedKey);
    }

    @Test
    void testVerifyCode_WhenCodeIsValid() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        String expectedKey = "verification:code:" + email;
        
        when(valueOperations.get(expectedKey)).thenReturn(code);

        // Act
        boolean result = verificationService.verifyCode(email, code);

        // Assert
        assertThat(result).isTrue();
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(expectedKey);
    }

    @Test
    void testVerifyCode_WhenCodeIsInvalid() {
        // Arrange
        String email = "test@example.com";
        String storedCode = "123456";
        String wrongCode = "654321";
        String expectedKey = "verification:code:" + email;
        
        when(valueOperations.get(expectedKey)).thenReturn(storedCode);

        // Act
        boolean result = verificationService.verifyCode(email, wrongCode);

        // Assert
        assertThat(result).isFalse();
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(expectedKey);
    }

    @Test
    void testVerifyCode_WhenNoCodeStored() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        String expectedKey = "verification:code:" + email;
        
        when(valueOperations.get(expectedKey)).thenReturn(null);

        // Act
        boolean result = verificationService.verifyCode(email, code);

        // Assert
        assertThat(result).isFalse();
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(expectedKey);
    }

    @Test
    void testRemoveVerificationCode() {
        // Arrange
        String email = "test@example.com";
        String expectedKey = "verification:code:" + email;
        
        when(redisTemplate.delete(expectedKey)).thenReturn(true);

        // Act
        verificationService.removeVerificationCode(email);

        // Assert
        verify(redisTemplate).delete(expectedKey);
    }
} 