package com.example.githubprconsumer.global.application;

import com.example.githubprconsumer.global.exception.SystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EncryptService {

    private final AesBytesEncryptor encryptor;

    public String encrypt(String input) {
        try {
            byte[] encryptedBytes = encryptor.encrypt(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new SystemException("양방향 암호화가 실패했습니다. input : " + input);
        }
    }

    public String decrypt(String encryptedString) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
            byte[] decryptedBytes = encryptor.decrypt(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SystemException("양방향 복호화가 실패했습니다. input : " + encryptedString);
        }
    }
}
