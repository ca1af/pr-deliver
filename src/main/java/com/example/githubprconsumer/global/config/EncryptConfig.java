package com.example.githubprconsumer.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

@Configuration
public class EncryptConfig {

    @Value("${encrypt.key}")
    private String encryptionKey;

    @Value("${encrypt.salt}")
    private String base64salt;

    @Bean
    public AesBytesEncryptor aesBytesEncryptor() {
        return new AesBytesEncryptor(encryptionKey, base64salt);
    }
}