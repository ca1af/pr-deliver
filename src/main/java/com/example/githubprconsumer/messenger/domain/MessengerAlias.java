package com.example.githubprconsumer.messenger.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class MessengerAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long collaboratorId;

    private Long messengerId;

    private String login;

    private String alias;

    public MessengerAlias(Long collaboratorId, Long messengerId, String login, String alias) {
        this.collaboratorId = collaboratorId;
        this.messengerId = messengerId;
        this.login = login;
        this.alias = alias;
    }
}
