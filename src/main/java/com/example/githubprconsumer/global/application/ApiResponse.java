package com.example.githubprconsumer.global.application;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final String message;
    private final String status;
    private T data;

    public ApiResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public ApiResponse(String message, String status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public static <T> ApiResponse<T> ofSuccess(T data) {
        return new ApiResponse<>("정상적으로 처리되었습니다", "Success", data);
    }

    public static <T> ApiResponse<T> ofFail(String message) {
        return new ApiResponse<>(message, "Fail");
    }
}
