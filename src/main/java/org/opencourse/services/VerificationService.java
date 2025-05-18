package org.opencourse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 */
@Service
public class VerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${app.verification.code.expiration}")
    private long codeExpirationSeconds;
    
    @Autowired
    public VerificationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * 保存验证码
     * @param email 用户邮箱
     * @param code 验证码
     */
    public void saveVerificationCode(String email, String code) {
        String key = generateVerificationKey(email);
        redisTemplate.opsForValue().set(key, code, codeExpirationSeconds, TimeUnit.SECONDS);
    }
    
    /**
     * 获取验证码
     * @param email 用户邮箱
     * @return 验证码
     */
    public String getVerificationCode(String email) {
        String key = generateVerificationKey(email);
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 验证验证码
     * @param email 用户邮箱
     * @param code 验证码
     * @return 是否验证成功
     */
    public boolean verifyCode(String email, String code) {
        String storedCode = getVerificationCode(email);
        return storedCode != null && storedCode.equals(code);
    }
    
    /**
     * 删除验证码
     * @param email 用户邮箱
     */
    public void removeVerificationCode(String email) {
        String key = generateVerificationKey(email);
        redisTemplate.delete(key);
    }
    
    private String generateVerificationKey(String email) {
        return "verification:code:" + email;
    }
}
