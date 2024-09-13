package com.example.githubprconsumer.github.application;

import com.example.githubprconsumer.github.application.dto.GithubRepositoryResponseDto;
import com.example.githubprconsumer.github.domain.GithubRepository;
import com.example.githubprconsumer.github.domain.GithubRepositoryException;
import com.example.githubprconsumer.github.domain.GithubRepositoryJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class GithubRepositoryReaderTest {

    @Autowired
    private GithubRepositoryJpaRepository jpaRepository;

    @Autowired
    private GithubRepositoryReader githubRepositoryReader;

    @Test
    @DisplayName("저장된 Github 저장소를 ID로 조회하면 정상적으로 반환된다.")
    void testGetGithubRepository() {
        // Given
        GithubRepository repository = new GithubRepository("owner-login", "repository-full-name");
        jpaRepository.save(repository);

        // When
        GithubRepositoryResponseDto responseDto = githubRepositoryReader.getGithubRepository(repository.getId());

        // Then
        assertThat(responseDto.fullName()).isEqualTo("repository-full-name");
        assertThat(responseDto.webhookUrl()).isEqualTo(repository.getWebhookUrl());
    }

    @Test
    @DisplayName("저장된 Github 저장소를 ID로 조회할 때 존재하지 않으면 예외가 발생한다.")
    void testGetGithubRepository_NotFound() {
        // Given
        Long invalidId = 99999999L;

        // When & Then
        assertThatThrownBy(() -> githubRepositoryReader.getGithubRepository(invalidId))
                .isInstanceOf(GithubRepositoryException.GithubRepositoryNotFoundException.class);
    }
}