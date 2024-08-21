package com.example.githubprconsumer.github;

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

    public GithubRepository(String ownerLogin, String fullName) {
        this.ownerLogin = ownerLogin;
        this.fullName = fullName;
        this.webhookUrl = RandomStringGenerator.getRandomString(fullName);
        this.assigneeCount = 1;
    }

    public void updateAssigneeCount(int count) {
        this.assigneeCount = count;
    }
}
