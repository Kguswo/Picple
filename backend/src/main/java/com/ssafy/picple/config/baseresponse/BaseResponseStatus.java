package com.ssafy.picple.config.baseresponse;

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
	JWT_KEY_GENERATE_ERROR(false, 2005, "JWT 토큰 발행 중 에러가 발생하였습니다."),
	INVALID_REFRESH_TOKEN(false, 2006, "리프레시 토큰이 유효하지 않습니다."),

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
	NOT_FOUND_USER(false, 3018, "유저 정보가 없습니다."),
	INVALID_PASSWORD(false, 3019, "비밀번호가 일치하지 않습니다."),
	PASSWORD_ENCRYPTION_ERROR(false, 3020, "암호화된 비밀번호가 일치하지 않습니다."),
	ERROR_MODIFY_NICKNAME(false, 3021, "닉네임 변경 중 오류가 발생하였습니다."),
	ERROR_MODIFY_PASSWORD(false, 3022, "비밀번호 변경 중 오류가 발생하였습니다."),
	EMPTY_REQUEST_PASSWORD(false, 3023, "비밀번호를 입력해주세요."),

	// 3100 ~~ 3199 : 김현재
	// boardlike
	GET_LIKE_EMPTY(false, 3101, "좋아요 기록이 없습니다."),
	ALREADY_LIKED(false, 3102, "이미 좋아요 한 상태입니다."),
	ALREADY_UNLIKED(false, 3103, "이미 좋아요하지 않은 상태입니다."),

	// photo
	GET_PHOTO_EMPTY(false, 3110, "해당 사진이 없습니다."),
	ALREADY_SHARED(false, 3111, "이미 공유된 사진입니다."),
	FILE_UPLOAD_ERROR(false, 3120, "파일 업로드 중 오류가 발생했습니다."),
	FILE_DOWNLOAD_ERROR(false, 3121, "파일 다운로드 중 오류가 발생했습니다."),
	FILE_DELETE_ERROR(false, 3122, "파일 삭제 중 오류가 발생했습니다."),
	FILE_NOT_FOUND_ERROR(false, 3123, "파일을 찾을 수 없습니다."),

	// calendar
	GET_CALENDAR_EMPTY(false, 3130, "캘린더에 일치하는 항목이 없습니다."),
	GET_PHOTO_USER_EMPTY(false, 3140, "photoUser에 일치하는 항목이 없습니다."),
	NOT_EQUAL_USER_ID(false, 3141, "로그인 한 유저와 캘린더 작성자가 일치하지 않습니다."),

	// board
	GET_BOARD_EMPTY(false, 3150, "유효하지 않은 boardId값입니다"),

	// 3200 ~~ 3299 : 염규영
	// background
	INVALID_BACKGROUND_ID(false, 3202, "올바르지 않은 배경화면 ID입니다."),
	DELETE_BACKGROUND_ERROR(false, 3203, "배경 사진 삭제에 실패하였습니다."),
	BACKGROUND_UPLOAD_ERROR(false, 3204, "배경 사진 생성에 실패하였습니다."),
	URL_EMPTY_ERROR(false, 3225, "URL이 비어있습니다."),
	GENERATING_FILE_NAME_ERROR(false, 3227, "파일명을 생성하는데 실패하였습니다."),

	// background - ai
	JSON_PARSING_ERROR(false, 3206, "JSON을 처리할 수 없습니다."),
	AI_CLIENT_ERROR(false, 3207, "AI 클라이언트 오류가 발생하였습니다."),
	AI_SERVER_ERROR(false, 3208, "AI 서버 오류가 발생하였습니다."),
	BLOCKING_ERROR(false, 3228, "블로킹에 실패하였습니다."),
	NULL_PROMPT_ERROR(false, 3230, "프롬프트가 비어있습니다."),

	// background - local
	INVALID_IMAGE_FORMAT(false, 3220, "지원하지 않는 확장자입니다."),
	FILE_SIZE_TOO_BIG(false, 3222, "사진 크기가 너무 큽니다."),
	FILE_SIZE_TOO_SMALL(false, 3223, "사진 크기가 너무 작습니다."),
	FILE_CONVERSION_ERROR(false, 3226, "파일을 변환하는데 실패하였습니다."),

	// booth
	GENERATE_BOOTH_SUCCESSFULLY(true, 3257, "부스를 성공적으로 생성하였습니다."),
	BOOTH_GENERATION_ERROR(false, 3250, "부스를 생성할 수 없습니다."),
	JOIN_BOOTH_SUCCESSFULLY(true, 3251, "부스에 참가하였습니다."),
	FAILED_TO_JOIN_BOOTH(false, 3252, "부스 참가에 실패하였습니다."),
	FULL_BOOTH_ERROR(false, 3253, "이미 최대 인원이 참여 중입니다."),
	LEAVE_BOOTH_SUCCESSFULLY(true, 3254, "부스에서 떠났습니다."),
	FAILED_TO_LEAVE_BOOTH(false, 3255, "부스 떠나기에 실패하였습니다."),
	BOOTH_NOT_FOUND(false, 3256, "부스를 찾을 수 없습니다."),
	BOOTH_INFORMATION_ERROR(false, 3257, "부스 정보를 가져올 수 없습니다."),

	// websocket
	WEBSOCKET_CONNECTION_ERROR(false, 3280, "WebSocket 연결 중 오류가 발생하였습니다."),
	WEBSOCKET_MESSAGE_PROCESSING_ERROR(false, 3281, "WebSocket 메시지 처리 중 오류가 발생하였습니다."),
	WEBSOCKET_BOOTH_CREATION_ERROR(false, 3282, "부스 생성 중 오류가 발생하였습니다."),
	WEBSOCKET_BOOTH_JOIN_ERROR(false, 3283, "부스 참여 중 오류가 발생하였습니다."),
	BACKGROUND_CHANGE_ERROR(false, 3284, "배경 변경에 실패하였습니다."),

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
