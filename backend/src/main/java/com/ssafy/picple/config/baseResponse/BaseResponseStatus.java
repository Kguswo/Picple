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
	INVALID_USER_JWT(false, 2003, "권한이 없는 유저의 접근입니다."),
	JWT_GET_USER_ERROR(false, 2004, "User 권한 인증 중 에러가 발생하였습니다."),
	JWT_KEY_GENERATE_ERROR(false, 2004, "User 권한 인증 중 에러가 발생하였습니다."),

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
    NOT_FOUND_USER(false, 3018, "값을 불러오는데 실패하였습니다."),
    INVALID_PASSWORD(false, 3019, "비밀번호가 일치하지 않습니다."),

	// 3100 ~~ 3199 : 김현재
	GET_LIKE_EMPTY(false, 3101, "좋아요 기록이 없습니다."),
	ALREADY_LIKED(false, 3102, "이미 좋아요 한 상태입니다."),
	ALREADY_UNLIKED(false, 3102, "이미 좋아요 한 상태입니다."),

	// 3200 ~~ 3299 : 염규영
	INVALID_BACKGROUND_ID(false, 3202, "올바르지 않은 배경화면 ID입니다."),
	DELETE_BACKGROUND_ERROR(false, 3203, "배경 사진 삭제에 실패하였습니다."),
	AI_BACKGROUND_GENERATION_ERROR(false, 3204, "AI 배경 사진 생성에 실패하였습니다."),
	LOCAL_BACKGROUND_UPLOAD_ERROR(false, 3205, "로컬 배경 사진 추가에 실패하였습니다."),
	AI_RESPONSE_JSON_PARSING_ERROR(false, 3206, "JSON을 처리할 수 없습니다."),
	AI_CLIENT_ERROR(false, 3207, ""),
	AI_SERVER_ERROR(false, 3208, ""),


	/**
	 * 4000 : Database, Server
	 */
	DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),

	/**
	 * 5000 : Server 오류
	 */
	SERVER_ERROR(false, 5000, "서버와의 연결에 실패하였습니다.");

	private final boolean isSuccess;
	private final int code;
	private final String message;

	private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
		this.isSuccess = isSuccess;
		this.code = code;
		this.message = message;
	}
}
