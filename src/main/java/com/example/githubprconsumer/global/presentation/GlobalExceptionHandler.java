package com.example.githubprconsumer.global.presentation;

import com.example.githubprconsumer.global.application.ApiResponse;
import com.example.githubprconsumer.global.exception.BadRequestException;
import com.example.githubprconsumer.global.exception.NotAuthorizedException;
import com.example.githubprconsumer.global.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleException(Exception e) {
        log.error("Exception : ", e);
        return ApiResponse.ofFail(e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleBadRequest(Exception e) {
        return ApiResponse.ofFail(e.getMessage());
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<String> handleNotAuthorized(Exception e) {
        return ApiResponse.ofFail(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<String> handleNotFound(Exception e) {
        return ApiResponse.ofFail(e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleSystemException(Exception e) {
        return ApiResponse.ofFail(e.getMessage());
    }

}
