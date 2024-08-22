package com.example.githubprconsumer.member;

import com.example.githubprconsumer.global.TimeStamp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@NoArgsConstructor
@Getter
public class Member extends TimeStamp implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String authToken;

    private boolean isValid;

    public Member(String login) {
        this.login = login;
        this.isValid = false;
    }

    @Override
    public boolean isNew() {
        return this.getCreatedAt() == null;
    }
}
