package com.example.githubprconsumer.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultMessageService {

    private final DefaultMessageRepository defaultMessageRepository;

    public void save(){
        // 깃허브 API 를 사용해서 외부 정보를 가져오도록 조정한다.
    }
}
