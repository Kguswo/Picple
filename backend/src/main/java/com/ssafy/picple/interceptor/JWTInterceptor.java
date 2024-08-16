package com.ssafy.picple.interceptor;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Spring MVC의 인터셉터를 구현한 것
 * 각 요청에 대해 JWT를 검증하는 역할
 * 주로 사용자의 인증 상태를 확인하기 위해 사용
 */

@Component
@RequiredArgsConstructor
public class JWTInterceptor implements HandlerInterceptor {

	private final JWTUtil jwtUtil;

	/**
	 * 모든 요청이 컨트롤러에 도달하기 전에 실행됨
	 * JWT 토큰을 검증, 유효하지 않은 경우 예외 발생
	 *
	 * @param request  현재의 HTTP 요청
	 * @param response 현재의 HTTP 응답
	 * @param handler  호출될 핸들러 (컨트롤러 메서드)
	 * @return true일 경우 요청이 정상적으로 진행, false일 경우 요청이 중단
	 * @throws BaseException JWT가 없거나 유효하지 않은 경우 발생
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws BaseException {
		if (CorsUtils.isPreFlightRequest(request)) {
			return true;
		}

		String token = request.getHeader("X-ACCESS-TOKEN");

		if (token == null) {
			// 토큰이 존재하지 않으면 예외 발생 (JWT가 비어 있음)
			throw new BaseException(EMPTY_JWT);
		}
		if (!jwtUtil.verifyAccessToken(token)) {
			throw new BaseException(INVALID_JWT);
		}

		Long userId = jwtUtil.getUserIdByToken(token);
		request.setAttribute("userId", userId);

		// 요청이 정상적으로 진행될 수 있도록 true 반환
		return true;
	}
}
