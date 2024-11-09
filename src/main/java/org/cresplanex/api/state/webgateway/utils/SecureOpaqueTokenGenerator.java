package org.cresplanex.api.state.webgateway.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureOpaqueTokenGenerator {
    private static final int TOKEN_LENGTH = 64; // トークンの長さ（バイト数）

    public static String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
