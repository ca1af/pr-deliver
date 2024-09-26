package com.example.githubprconsumer.global.application;

import lombok.Getter;

@Getter
public class CustomApiResponse<T> {
    private final String message;
    private final String status;
    private T data;

    public CustomApiResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public CustomApiResponse(String message, String status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public static <T> CustomApiResponse<T> ofSuccess(T data) {
        return new CustomApiResponse<>("정상적으로 처리되었습니다", "Success", data);
    }

    public static <T> CustomApiResponse<T> ofFail(String message) {
        return new CustomApiResponse<>(message, "Fail");
    }

    public static CustomApiResponse<Void> ofSuccess(){
        return new CustomApiResponse<>("정상적으로 처리되었습니다", "Success");
    }
}
