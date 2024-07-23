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

	// 3001 ~~ 3099 : 염규영
	GET_USER_EMPTY(false, 3001, "등록된 유저가 없습니다."),

	// 3100 ~~ 3199 : 김현재
	GET_LIKE_EMPTY(false, 3101, "좋아요 기록이 없습니다."),
	ALREADY_LIKED(false, 3102, "이미 좋아요 한 상태입니다."),
	ALREADY_UNLIKED(false, 3103, "이미 좋아요하지 않은 상태입니다."),
	GET_PHOTO_EMPTY(false, 3110, "해당 사진이 없습니다."),
	ALREADY_SHARED(false, 3111, "이미 공유된 사진입니다."),
	FILE_UPLOAD_ERROR(false, 3120, "파일 업로드 중 오류가 발생했습니다."),
	FILE_DOWNLOAD_ERROR(false, 3121, "파일 다운로드 중 오류가 발생했습니다."),
	FILE_DELETE_ERROR(false, 3122, "파일 삭제 중 오류가 발생했습니다."),
	GET_CALENDAR_EMPTY(false, 3130, "캘린더에 삭제할 파일이 없습니다."),
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
