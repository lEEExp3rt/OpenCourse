package org.opencourse.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.opencourse.utils.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 邮件服务
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationService verificationService;
    
    @Autowired
    public EmailService(JavaMailSender mailSender, VerificationService verificationService) {
        this.mailSender = mailSender;
        this.verificationService = verificationService;
    }
    
    /**
     * 发送验证码邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param purpose 用途描述
     * @return 生成的验证码
     * @throws MessagingException 邮件发送异常
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
    
    /**
     * 创建验证码邮件模板
     * @param code 验证码
     * @param purpose 用途描述
     * @return HTML格式的邮件内容
     */
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
