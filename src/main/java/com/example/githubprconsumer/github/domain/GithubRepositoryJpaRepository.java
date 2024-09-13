package com.example.githubprconsumer.github.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GithubRepositoryJpaRepository extends JpaRepository<GithubRepository, Long> {

    Optional<GithubRepository> findByFullName(String fullName);

    Optional<GithubRepository> findByWebhookUrl(String webhookUrl);

    List<GithubRepository> findAllByOwnerLogin(String ownerLogin);
}
