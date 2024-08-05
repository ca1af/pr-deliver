package com.example.githubprconsumer.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {

    Optional<MessageTemplate> findByGithubRepositoryId(String memberId);

}
