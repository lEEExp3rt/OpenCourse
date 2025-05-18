package org.opencourse.utils;

import java.security.SecureRandom;

/**
 * 验证码生成工具类
 */
public class VerificationCodeGenerator {
    
    private static final String CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * 生成6位数字验证码
     * @return 验证码
     */
    public static String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        return code.toString();
    }
}
