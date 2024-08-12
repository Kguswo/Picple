package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseresponse.BaseException;

/**
 * EmailService 인터페이스
 * 이메일 발송, 인증 코드 검증 기능 제공
 */
public interface EmailService {

	/**
	 * 인증코드를 포함한 이메일을 발송
	 *
	 * @param email 인증 코드를 발송할 이메일 주소
	 * @return 인증 코드
	 * @throws BaseException 이메일 발송 실패 시 발생
	 */
	String sendEmail(String email) throws BaseException;

	/**
	 * 이메일과 함께 전송된 인증 코드 검증
	 *
	 * @param email 인증 코드를 검증할 이메일 주소
	 * @param code 입력된 인증 코드
	 * @return "SUCCESS" 검증 성공 시 반환
	 * @throws BaseException 검증 실패 시 발생
	 */
	String verifyEmailCode(String email, String code) throws BaseException;
}
