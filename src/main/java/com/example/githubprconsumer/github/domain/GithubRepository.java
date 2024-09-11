package com.example.githubprconsumer.github.domain;

import com.example.githubprconsumer.global.RandomStringGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class GithubRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String ownerLogin;
    private String fullName;
    private String webhookUrl;
    private Integer assigneeCount;
    @Column(nullable = false)
    private boolean isActiveWebhook;

    public GithubRepository(String ownerLogin, String fullName) {
        this.ownerLogin = ownerLogin;
        this.fullName = fullName;
        this.webhookUrl = RandomStringGenerator.getRandomString(fullName);
        this.assigneeCount = 1;
        this.isActiveWebhook = false;
    }

    public void activateWebhook(){
        this.isActiveWebhook = true;
    }

    public void updateAssigneeCount(int inputCount, int collaboratorCount) {
        if (inputCount < 1){
            throw new CollaboratorException.InvalidCollaboratorCountException();
        }

        if (inputCount >= collaboratorCount - 1) {
            throw new CollaboratorException.InvalidCollaboratorCountException();
        }

        this.assigneeCount = inputCount;
    }

    public boolean isMyRepo(String login){
        return ownerLogin.equals(login);
    }
}
