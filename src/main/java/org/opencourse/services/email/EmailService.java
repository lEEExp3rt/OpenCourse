package org.opencourse.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.opencourse.utils.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email service manager.
 * 
 * @author LJX
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationService verificationService;

    /**
     * Constructor.
     * 
     * @param mailSender          The mail sender.
     * @param verificationService The verification service.
     */
    @Autowired
    public EmailService(JavaMailSender mailSender, VerificationService verificationService) {
        this.mailSender = mailSender;
        this.verificationService = verificationService;
    }

    /**
     * Send verification code to the specified email address.
     * 
     * @param to      Recipient email address.
     * @param subject Email subject.
     * @param purpose Purpose of the email (e.g., "Register", "Reset Password").
     * @return Generated verification code.
     * @throws MessagingException If an error occurs while sending the email.
     */
    public String sendVerificationCode(String to, String subject, String purpose) throws MessagingException {
        String verificationCode = VerificationCodeGenerator.generateCode();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);

        String htmlContent = createVerificationEmailTemplate(verificationCode, purpose);
        helper.setText(htmlContent, true);

        mailSender.send(message);

        // 存储验证码
        verificationService.saveVerificationCode(to, verificationCode);

        return verificationCode;
    }

    private String createVerificationEmailTemplate(String code, String purpose) {
        return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;'>"
                + "<h2 style='color: #333; text-align: center;'>OpenCourse 验证码</h2>"
                + "<p style='color: #666; font-size: 16px;'>您好，</p>"
                + "<p style='color: #666; font-size: 16px;'>您正在进行 " + purpose + "，您的验证码是：</p>"
                + "<div style='background-color: #f5f5f5; padding: 10px; text-align: center; margin: 20px 0;'>"
                + "<span style='font-size: 24px; font-weight: bold; letter-spacing: 5px;'>" + code + "</span>"
                + "</div>"
                + "<p style='color: #666; font-size: 16px;'>验证码有效期为5分钟，请勿将验证码泄露给他人。</p>"
                + "<p style='color: #666; font-size: 16px;'>如果这不是您的操作，请忽略此邮件。</p>"
                + "<p style='color: #666; font-size: 14px; margin-top: 30px; text-align: center;'>此邮件由系统自动发送，请勿回复。</p>"
                + "</div>";
    }
}
