package org.opencourse.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Verification service manager.
 * 
 * @author LJX
 */
@Service
public class VerificationService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.verification.code.expiration}")
    private long codeExpirationSeconds;

    /**
     * Constructor.
     * 
     * @param redisTemplate Redis template.
     */
    @Autowired
    public VerificationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Save verification code.
     * 
     * @param email User email.
     * @param code  Verification code.
     */
    public void saveVerificationCode(String email, String code) {
        String key = generateVerificationKey(email);
        redisTemplate.opsForValue().set(key, code, codeExpirationSeconds, TimeUnit.SECONDS);
    }

    /**
     * Get verification code.
     * 
     * @param email The user's email.
     * @return The verification code or null if not found.
     */
    public String getVerificationCode(String email) {
        String key = generateVerificationKey(email);
        return redisTemplate.opsForValue().get(key);
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
        redisTemplate.delete(key);
    }

    private String generateVerificationKey(String email) {
        return "verification:code:" + email;
    }
}
