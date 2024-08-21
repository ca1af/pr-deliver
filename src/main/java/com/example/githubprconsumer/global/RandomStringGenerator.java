package com.example.githubprconsumer.global;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RandomStringGenerator {

    private RandomStringGenerator() {
        throw new IllegalStateException("Utility class");
    }

    private static MessageDigest getMessageDigest(){
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e){
            throw new NotFoundException("SHA-256 not available");
        }
    }

    public static String getRandomString(String baseString) {
        byte[] hash = getMessageDigest().digest(baseString.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.substring(0, 8); // 앞의 8자리만 사용
    }
}
