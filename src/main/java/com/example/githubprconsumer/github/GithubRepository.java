package com.example.githubprconsumer.github;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GithubRepository {
    // 1. 레포지토리에 속한 콜라보레이터들에게 리뷰 할당을 진행한다.
    // 2.

    private String ownerName;
    private String name;
    // 위 둘을 합치면 fullName
    private List<Collaborator> collaboratorList = new ArrayList<>();

    public String getFullName(){
        return ownerName + "/" + name;
    }

    // 걍 니가 그것도 해라...깃헙 API 호출 정보도 갖고잇을래?
    // 그게 낫겄네

}
