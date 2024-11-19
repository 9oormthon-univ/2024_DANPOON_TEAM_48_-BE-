package com.example.mesh_backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //JWT
    INVALID_TOKEN(403, "JWT003", "유효하지 않은 토큰입니다.");



    private int status;
    private final String code;
    private final String message;

    /**
     * 전체 ErrorCode 리스트를 반환하는 메서드
     */
    public static List<ErrorCode> getAllErrorCodes() {
        return Arrays.stream(ErrorCode.values()).collect(Collectors.toList());
    }
}
