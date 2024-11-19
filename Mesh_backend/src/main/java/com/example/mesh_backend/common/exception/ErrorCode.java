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

    //login
    INVALID_INPUT_VALUE(400, "COMMON001", "유효하지 않은 입력 값입니다."),
    USER_NOT_FOUND(404, "USER001", "유저를 찾을 수 없습니다."),
    INVALID_TOKEN(401, "AUTH001", "유효하지 않은 토큰입니다."),
    EMAIL_ALREADY_EXISTS(400, "USER002", "이미 가입된 이메일입니다."),
    DUPLICATE_NICKNAME(400, "USER003", "중복된 닉네임입니다."),
    LOGIN_REQUIRED(401, "AUTH002", "로그인이 필요합니다."),
    INTERNAL_SERVER_ERROR(500, "COMMON002", "서버 오류가 발생했습니다."),
    USER_ID_REQUIRED(400, "USER004", "유저 ID가 필요합니다."),
    KAKAO_API_ERROR(500, "KAKAO001", "카카오 API 호출 중 오류가 발생했습니다.");



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
