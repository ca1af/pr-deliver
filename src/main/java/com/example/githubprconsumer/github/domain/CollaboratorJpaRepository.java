package com.example.githubprconsumer.github.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollaboratorJpaRepository extends JpaRepository<Collaborator, Long> {
    List<Collaborator> findAllByRepositoryId(Long repositoryId);
    Integer countByRepositoryId(Long repositoryId);
    Optional<Collaborator> findByRepositoryIdAndLogin(Long repositoryId, String login);
}
