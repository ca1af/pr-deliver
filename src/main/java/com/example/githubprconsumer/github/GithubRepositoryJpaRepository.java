package com.example.githubprconsumer.github;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubRepositoryJpaRepository extends JpaRepository<GithubRepository, Long> {

    boolean existsByFullName(String fullName);

}
