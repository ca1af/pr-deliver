package com.example.githubprconsumer.messenger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Messenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long repositoryId;

    @Enumerated(EnumType.STRING)
    private MessengerType messengerType;

    @Column(nullable = false)
    private String webhookUrl;

    public Messenger(Long repositoryId, MessengerType messengerType, String webhookUrl) {
        this.repositoryId = repositoryId;
        this.messengerType = messengerType;
        this.webhookUrl = webhookUrl;
    }
}
