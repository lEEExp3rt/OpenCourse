package org.opencourse.utils;

import java.security.SecureRandom;

/**
 * Verification code generator.
 * 
 * @author LJX
 */
public class VerificationCodeGenerator {
    
    private static final String CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generate a random verification code of 6 digits.
     * 
     * @return The generated verification code.
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
