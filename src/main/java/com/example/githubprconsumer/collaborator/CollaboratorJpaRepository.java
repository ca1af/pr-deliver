package com.example.githubprconsumer.collaborator;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaboratorJpaRepository extends JpaRepository<Collaborator, Long> {
    List<Collaborator> findByRepositoryId(Long repositoryId);
    Integer countByRepositoryId(Long repositoryId);
}
