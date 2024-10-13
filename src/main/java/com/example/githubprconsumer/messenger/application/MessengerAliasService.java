package com.example.githubprconsumer.messenger.application;

import com.example.githubprconsumer.messenger.application.dto.MessengerAliasAddRequestDto;
import com.example.githubprconsumer.messenger.application.dto.MessengerAliasResponseDto;
import com.example.githubprconsumer.messenger.domain.MessengerAlias;
import com.example.githubprconsumer.messenger.domain.MessengerAliasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<String> applyMessengerAlias(Long messengerId, List<String> collaboratorLogins){
        List<MessengerAlias> messengerAliases = messengerAliasRepository.findAllByMessengerIdAndLoginIn(messengerId, collaboratorLogins);

        Map<String, String> aliasMap = messengerAliases.stream()
                .collect(Collectors.toMap(MessengerAlias::getLogin, MessengerAlias::getAlias));

        return collaboratorLogins.stream()
                .map(login -> aliasMap.getOrDefault(login, login))
                .toList();
    }

    public boolean isAliasExists(Long messengerId){
        return messengerAliasRepository.existsByMessengerId(messengerId);
    }
}
