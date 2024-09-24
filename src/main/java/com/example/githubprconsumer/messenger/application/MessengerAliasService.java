package com.example.githubprconsumer.messenger.application;

import com.example.githubprconsumer.messenger.application.dto.MessengerAliasAddRequestDto;
import com.example.githubprconsumer.messenger.application.dto.MessengerAliasResponseDto;
import com.example.githubprconsumer.messenger.domain.MessengerAlias;
import com.example.githubprconsumer.messenger.domain.MessengerAliasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessengerAliasService {

    private final MessengerAliasRepository messengerAliasRepository;

    public void addMessengerAlias(List<MessengerAliasAddRequestDto> messengerAliasAddRequestDtoList){
        List<MessengerAlias> messengerAliases = messengerAliasAddRequestDtoList
                .stream()
                .map(MessengerAliasAddRequestDto::toEntity)
                .toList();

        messengerAliasRepository.saveAll(messengerAliases);
    }

    public List<MessengerAliasResponseDto> findAllByMessengerId(Long messengerId){
        List<MessengerAlias> messengerAliasList = messengerAliasRepository.findAllByMessengerId(messengerId);
        return messengerAliasList
                .stream()
                .map(MessengerAliasResponseDto::of)
                .toList();
    }
}
