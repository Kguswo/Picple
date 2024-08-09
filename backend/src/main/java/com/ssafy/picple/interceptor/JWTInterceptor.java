package com.ssafy.picple.interceptor;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTInterceptor implements HandlerInterceptor {

	private final JWTUtil jwtUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws BaseException {
		if (CorsUtils.isPreFlightRequest(request)) {
			return true;
		}

		String token = request.getHeader("X-ACCESS-TOKEN");

		if (token == null) {
			throw new BaseException(EMPTY_JWT);
		}
		if (!jwtUtil.verifyAccessToken(token)) {
			throw new BaseException(INVALID_JWT);
		}

		Long userId = jwtUtil.getUserIdByToken(token);
		request.setAttribute("userId", userId);

		return true;
	}
}
