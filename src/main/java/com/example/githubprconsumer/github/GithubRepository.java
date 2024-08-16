package com.example.githubprconsumer.github;

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
    private Long memberId;
    private String fullName;

    public GithubRepository(Long memberId, String fullName) {
        this.memberId = memberId;
        this.fullName = fullName;
    }
}
