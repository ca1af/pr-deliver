package com.example.githubprconsumer.bot.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubApiEventListener {

    @EventListener
    public void invalidPermissionEvent(InvalidPermissionEvent invalidPermissionEvent){
        // 멤버를 찾아 알림을 보내도록 한다.
    }
}
