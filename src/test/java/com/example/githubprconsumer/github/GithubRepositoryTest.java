package com.example.githubprconsumer.github;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class GithubRepositoryTest {

    @Test
    void createTest() {
        GithubRepository githubRepository = new GithubRepository("Login", "FOO");
        Assertions.assertThat(githubRepository).isNotNull();
        Assertions.assertThat(githubRepository.getAssigneeCount()).isOne();
    }

    @Test
    void webhookUrlTest() {
        GithubRepository githubRepository = new GithubRepository("Login", "FOO");
        Assertions.assertThat(githubRepository.getWebhookUrl()).hasSize(8);
    }
}