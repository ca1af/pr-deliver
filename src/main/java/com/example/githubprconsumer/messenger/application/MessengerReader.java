package com.example.githubprconsumer.messenger.application;

import com.example.githubprconsumer.messenger.application.dto.MessengerAliasResponseDto;
import com.example.githubprconsumer.messenger.application.dto.MessengerResponseDto;
import com.example.githubprconsumer.messenger.domain.Messenger;
import com.example.githubprconsumer.messenger.domain.MessengerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessengerReader {

    private final MessengerJpaRepository messengerJpaRepository;

    private final MessengerAliasService messengerAliasService;

    public List<MessengerResponseDto> findAllByRepositoryId(Long repositoryId){
        List<Messenger> messengerList = messengerJpaRepository.findAllByRepositoryId(repositoryId);
        return messengerList
                .stream()
                .map(this::getMessengerResponseDto)
                .toList();
    }

    private MessengerResponseDto getMessengerResponseDto(Messenger each) {
        List<MessengerAliasResponseDto> messengerAliasResponseDtoList = messengerAliasService.findAllByMessengerId(each.getId());
        return MessengerResponseDto.of(each, messengerAliasResponseDtoList);
    }
}
