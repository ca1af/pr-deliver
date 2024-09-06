package com.example.githubprconsumer.global;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class EncryptServiceTest {
    private EncryptService encryptService;

    @BeforeEach
    void setUp() {
        String encryptionKey = "test-encryption-key-1234567890123456";
        String salt = KeyGenerators.string().generateKey();
        AesBytesEncryptor encryptor = new AesBytesEncryptor(encryptionKey, salt);
        encryptService = new EncryptService(encryptor);
    }

    @Test
    @DisplayName("주어진 문자열을 암호화하고 복호화했을 때 원래 값으로 돌아온다.")
    void testEncryptAndDecrypt_Success() {
        // Given
        String originalString = "Hello, World!";

        // When
        String encryptedString = encryptService.encrypt(originalString);
        String decryptedString = encryptService.decrypt(encryptedString);

        // Then
        assertThat(decryptedString).isEqualTo(originalString);
    }

    @Test
    @DisplayName("잘못된 암호화된 문자열을 복호화하려고 하면 예외가 발생한다.")
    void testDecrypt_InvalidInput() {
        // Given
        String invalidEncryptedString = "invalidEncryptedString";

        // When & Then
        assertThatThrownBy(() -> encryptService.decrypt(invalidEncryptedString))
                .isInstanceOf(SystemException.class)
                .hasMessageContaining("양방향 복호화가 실패했습니다.");
    }

    @Test
    @DisplayName("암호화 시 비어있는 문자열도 처리된다.")
    void testEncryptAndDecrypt_EmptyString() {
        // Given
        String emptyString = "";

        // When
        String encryptedString = encryptService.encrypt(emptyString);
        String decryptedString = encryptService.decrypt(encryptedString);

        // Then
        assertThat(decryptedString).isEqualTo(emptyString);
    }
}