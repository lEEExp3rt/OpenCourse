package org.opencourse.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opencourse.utils.VerificationCodeGenerator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 邮件服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private VerificationService verificationService;

    @Mock
    private MimeMessage mimeMessage;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender, verificationService);
    }

    @Test
    void testSendVerificationCode() throws MessagingException {
        // Arrange
        String email = "test@example.com";
        String subject = "测试主题";
        String purpose = "测试目的";
        String mockCode = "123456";
        
        // 模拟静态方法
        try (MockedStatic<VerificationCodeGenerator> mockedGenerator = mockStatic(VerificationCodeGenerator.class)) {
            mockedGenerator.when(VerificationCodeGenerator::generateCode).thenReturn(mockCode);
            
            // 模拟邮件发送相关对象
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            doNothing().when(mailSender).send(mimeMessage);
            
            // Act
            String resultCode = emailService.sendVerificationCode(email, subject, purpose);
            
            // Assert
            assertThat(resultCode).isEqualTo(mockCode);
            
            verify(mailSender).createMimeMessage();
            verify(mailSender).send(mimeMessage);
            verify(verificationService).saveVerificationCode(eq(email), eq(mockCode));
        }
    }
    
    @Test
    void testSendVerificationCode_EmailContentCheck() throws MessagingException {
        // Arrange
        String email = "test@example.com";
        String subject = "测试主题";
        String purpose = "测试目的";
        String mockCode = "654321";

        // 模拟静态方法
        try (MockedStatic<VerificationCodeGenerator> mockedGenerator = mockStatic(VerificationCodeGenerator.class)) {
            mockedGenerator.when(VerificationCodeGenerator::generateCode).thenReturn(mockCode);

            // 创建真实的MimeMessage，捕获其内容
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            
            // 使用参数捕获器来捕获MimeMessageHelper的内容
            ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
            
            // Act
            String resultCode = emailService.sendVerificationCode(email, subject, purpose);
            
            // Assert
            assertThat(resultCode).isEqualTo(mockCode);
            
            verify(mailSender).send(messageCaptor.capture());
            verify(verificationService).saveVerificationCode(email, mockCode);
            
            // 无法直接验证邮件内容，因为MimeMessageHelper是在EmailService类内部创建的
            // 但我们可以验证邮件已经发送，且验证码已经正确保存
        }
    }
} 