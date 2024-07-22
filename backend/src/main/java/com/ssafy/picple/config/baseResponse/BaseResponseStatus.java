package com.ssafy.picple.config.baseResponse;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    JWT_GET_USER_ERROR(false,2004,"User 권한 인증 중 에러가 발생하였습니다."),
    JWT_KEY_GENERATE_ERROR(false,2004,"User 권한 인증 중 에러가 발생하였습니다."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // 3001 ~~ 3099 : 박성훈
    // 3001 ~~ 3015 : about email
    DUPLICATED_USER_EMAIL(false, 3002, "해당 이메일이 이미 존재합니다."),
    DUPLICATED_USER_NICKNAME(false, 3003, "해당 닉네임이 이미 존재합니다."),
    EMAIL_SEND_ERROR(false, 3003, "이메일 발송 중 문제가 생겼습니다."),
    USER_EMAIL_EMPTY(false, 3004, "이메일을 입력해주세요."),
    NOT_EQUAL_EMAIL_CODE(false, 3005, "인증 코드를 확인하세요."),
    NOT_EXISTS_CODE(false, 3006, "인증을 다시 시도하여 주십시오."),

    // about user service
    GET_USER_EMPTY(false, 3016, "등록된 유저가 없습니다."),
    FAILED_USER_SIGNUP(false, 3017, "등록된 유저가 없습니다."),

    // 3100 ~~ 3199 : 김현재

    // 3200 ~~ 3299 : 염규영

    /**
     * 4000 : Database, Server
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),

    /**
     * 5000 : Server 오류
     */
    SERVER_ERROR(false, 5000, "서버와의 연결에 실패하였습니다.")
    ;

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
