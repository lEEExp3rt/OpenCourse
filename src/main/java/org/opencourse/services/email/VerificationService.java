package org.opencourse.services.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verification service manager.
 * 
 * @author LJX
 */
@Service
public class VerificationService {
    
    // 使用ConcurrentHashMap存储验证码和过期时间
    private final Map<String, VerificationCodeRecord> codeMap = new ConcurrentHashMap<>();

    @Value("${app.verification.code.expiration}")
    private long codeExpirationSeconds;

    /**
     * Save verification code.
     * 
     * @param email User email.
     * @param code  Verification code.
     */
    public void saveVerificationCode(String email, String code) {
        String key = generateVerificationKey(email);
        long expirationTime = Instant.now().getEpochSecond() + codeExpirationSeconds;
        codeMap.put(key, new VerificationCodeRecord(code, expirationTime));
    }

    /**
     * Get verification code.
     * 
     * @param email The user's email.
     * @return The verification code or null if not found.
     */
    public String getVerificationCode(String email) {
        String key = generateVerificationKey(email);
        VerificationCodeRecord record = codeMap.get(key);
        
        if (record == null) {
            return null;
        }
        
        // 检查是否过期
        if (Instant.now().getEpochSecond() > record.expirationTime) {
            codeMap.remove(key); // 移除过期的验证码
            return null;
        }
        
        return record.code;
    }

    /**
     * Verify the code.
     * 
     * @param email User email.
     * @param code  Verification code.
     * @return True if the code is valid, false otherwise.
     */
    public boolean verifyCode(String email, String code) {
        String storedCode = getVerificationCode(email);
        return storedCode != null && storedCode.equals(code);
    }

    /**
     * Remove verification code.
     * 
     * @param email User email.
     */
    public void removeVerificationCode(String email) {
        String key = generateVerificationKey(email);
        codeMap.remove(key);
    }

    private String generateVerificationKey(String email) {
        return "verification:code:" + email;
    }
    
    /**
     * 验证码记录，包含验证码和过期时间
     */
    private static class VerificationCodeRecord {
        private final String code;
        private final long expirationTime;
        
        public VerificationCodeRecord(String code, long expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
        }
    }
}
