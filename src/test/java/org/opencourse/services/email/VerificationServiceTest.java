package org.opencourse.services.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 验证码服务测试类
 */
public class VerificationServiceTest {

    private VerificationService verificationService;

    private final long EXPIRATION_SECONDS = 300; // 5分钟

    @BeforeEach
    void setUp() {
        verificationService = new VerificationService();
        ReflectionTestUtils.setField(verificationService, "codeExpirationSeconds", EXPIRATION_SECONDS);
    }

    @Test
    void testSaveAndGetVerificationCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";

        // Act
        verificationService.saveVerificationCode(email, code);
        String retrievedCode = verificationService.getVerificationCode(email);

        // Assert
        assertThat(retrievedCode).isEqualTo(code);
    }

    @Test
    void testGetVerificationCode_WhenNotFound() {
        // Arrange
        String email = "nonexistent@example.com";

        // Act
        String result = verificationService.getVerificationCode(email);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void testVerifyCode_WhenCodeIsValid() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        
        verificationService.saveVerificationCode(email, code);

        // Act
        boolean result = verificationService.verifyCode(email, code);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void testVerifyCode_WhenCodeIsInvalid() {
        // Arrange
        String email = "test@example.com";
        String storedCode = "123456";
        String wrongCode = "654321";
        
        verificationService.saveVerificationCode(email, storedCode);

        // Act
        boolean result = verificationService.verifyCode(email, wrongCode);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void testVerifyCode_WhenNoCodeStored() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";

        // Act
        boolean result = verificationService.verifyCode(email, code);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void testRemoveVerificationCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        
        verificationService.saveVerificationCode(email, code);
        
        // Act
        verificationService.removeVerificationCode(email);
        String retrievedCode = verificationService.getVerificationCode(email);

        // Assert
        assertThat(retrievedCode).isNull();
    }
    
    @Test
    void testCodeExpiration() throws InterruptedException {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        
        // 设置一个非常短的过期时间用于测试
        ReflectionTestUtils.setField(verificationService, "codeExpirationSeconds", 1); // 1秒
        
        verificationService.saveVerificationCode(email, code);
        
        // 确保代码在获取之前是存在的
        String codeBeforeExpiration = verificationService.getVerificationCode(email);
        assertThat(codeBeforeExpiration).isEqualTo(code);
        
        // 等待过期
        Thread.sleep(1100); // 稍微超过1秒
        
        // Act
        String codeAfterExpiration = verificationService.getVerificationCode(email);
        
        // Assert
        assertThat(codeAfterExpiration).isNull();
    }
} 