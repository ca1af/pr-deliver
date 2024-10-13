package com.example.githubprconsumer.github.domain;

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
public class Assignee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long registerCollaboratorId;

    @Column(nullable = false)
    private Long assigneeCollaboratorId;

    private String assigneeLogin;

    public Assignee(Long registerCollaboratorId, Long assigneeCollaboratorId, String assigneeLogin) {
        this.registerCollaboratorId = registerCollaboratorId;
        this.assigneeCollaboratorId = assigneeCollaboratorId;
        this.assigneeLogin = assigneeLogin;
    }
}
