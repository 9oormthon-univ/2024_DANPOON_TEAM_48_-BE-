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
    KAKAO_API_ERROR(500, "KAKAO001", "카카오 API 호출 중 오류가 발생했습니다."),
    JSON_PARSING_ERROR(400, "COMMON001", "JSON 파싱 오류 발생"),
    UNKNOWN_ERROR(500, "COMMON003", "처리 중 오류 발생"),
    IO_ERROR(500,"IMAGE001","입출력 오류발생"),



    //mypage
    UNAUTHORIZED_USER(401, "AUTH003", "인증된 유저 정보가 없습니다."),
    ACCESS_DENIED(403, "AUTH004", "접근 권한이 없습니다."),
    SESSION_EXPIRED(401, "AUTH005", "세션이 만료되었습니다."),
    INVALID_CATEGORY_NAME(400, "DATA001", "유효하지 않은 카테고리 이름입니다."),
    EMPTY_REQUIRED_FIELD(400, "DATA002", "필수 입력 값이 비어 있습니다."),
    INVALID_FORMAT(400, "DATA003", "입력 데이터 형식이 유효하지 않습니다."),

    // AWS-S3
    FILE_CONVERT_FAIL(500, "S3_001", "파일 변환 실패"),
    FILE_PROCESSING_ERROR(500, "COMMON002", "파일 처리 오류 발생"),
    FILE_UPLOAD_FAIL(500, "S3_002", "파일 업로드 실패"),
    FILE_DELETE_FAIL(500, "S3_003", "파일 삭제 실패"),
    INVALID_S3_PATH(400, "S3_004", "유효하지 않은 S3 경로입니다."),

    //post
    POST_NOT_FOUND(404, "POST_NOT_FOUND", "해당 공고를 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(404,"UNAUTHORIZED_ACCESS" , "권한이 없습니다.");


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
