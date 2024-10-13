package com.example.githubprconsumer.github.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "collaborator", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"repositoryId", "login"})
})
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
