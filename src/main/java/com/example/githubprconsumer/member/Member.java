package com.example.githubprconsumer.member;

import com.example.githubprconsumer.global.TimeStamp;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@NoArgsConstructor
@Getter
public class Member extends TimeStamp implements Persistable<Long> {

    @Id
    private Long id;

    private String login;

    private String authToken;

    private boolean isValid;

    public Member(Long id, String login) {
        this.id = id;
        this.login = login;
        this.isValid = false;
    }

    public void addToken(String authToken){
        this.authToken = authToken;
        this.isValid = true;
    }

    @Override
    public boolean isNew() {
        return this.getCreatedAt() == null;
    }
}
