package com.example.githubprconsumer.github;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Collaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long repositoryId;
    private String login;
    private String avatar;
    private String htmlUrl;

    public Collaborator(Long repositoryId, String login, String avatar, String htmlUrl) {
        this.repositoryId = repositoryId;
        this.login = login;
        this.avatar = avatar;
        this.htmlUrl = htmlUrl;
    }
}
