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

    private String nickname;

    public Member(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    @Override
    public boolean isNew() {
        return this.getCreatedAt() == null;
    }
}
