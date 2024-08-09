package com.ssafy.picple.util;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ssafy.picple.config.baseResponse.BaseException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {

	// JWT 서명에 사용할 비밀 키
	@Value("${jwt.salt}")
	private String salt;

	// 액세스 토큰의 만료 시간
	@Value("${jwt.access-token.expiretime}")
	private long accessTokenExpireTime;

	// 리프레시 토큰의 만료 시간
	@Value("${jwt.refresh-token.expiretime}")
	private long refreshTokenExpireTime;

	// 주어진 사용자 ID를 포함하는 액세스 토큰 생성
	public String createAccessToken(long userId) {
		return generateToken(userId, "access-token", accessTokenExpireTime);
	}

	// 주어진 사용자 ID를 포함하는 리프레시 토큰 생성
	public String createRefreshToken(long userId) {
		return generateToken(userId, "refresh-token", refreshTokenExpireTime);
	}

	// 주어진 사용자 ID, 토큰의 주제, 만료 시간을 사용하여 JWT를 생성
	private String generateToken(long userId, String subject, long expireTime) {
		Claims claims = Jwts.claims()
				.setSubject(subject) // 토큰의 Subject 설정
				.setIssuedAt(new Date()) // 토큰 발행 시간 설정
				.setExpiration(new Date(System.currentTimeMillis() + expireTime)); // 만료 시간 설정

		claims.put("userId", userId);  // 사용자 ID를 Claims에 추가

		String jwt = Jwts.builder()
				.setHeaderParam("typ", "JWT") // Header에 JWT 타입 설정
				.setClaims(claims)  // Claims 설정
				.signWith(SignatureAlgorithm.HS256, this.generateKey()) // 서명 알고리즘 및 비밀 키 설정
				.compact(); // JWT 생성

		return jwt;
	}

	// Signature 설정에 들어갈 key 생성
	private byte[] generateKey() {
		byte[] key = null;
		try {
			key = salt.getBytes("UTF-8"); // 비밀 키를 UTF-8 인코딩으로 바이트 배열로 변환
		} catch (Exception ignored) {

		}
		return key;
	}

	// 주어진 토큰이 유효한지 검증
	public boolean verifyToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder()
					.setSigningKey(this.generateKey()) // 서명 키 설정
					.build()
					.parseClaimsJws(token); // 토큰 파싱 및 검증
			// 토큰의 만료 시간이 현재 시간 이후인지 확인
			return !claims.getBody().getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			return false; // 토큰이 만료된 경우 false 반환
		}
	}

	// TODO: 수정 필요 (작동 안 됨)
	// 토큰을 무효화하여 로그아웃 처리
	public void logout(String token) throws BaseException {
		try {
			// 만료 시간을 현재 시간으로 설정하여 토큰을 무효화
			Jws<Claims> claims = Jwts.parserBuilder()
					.setSigningKey(this.generateKey()) // 서명 키 설정
					.build()
					.parseClaimsJws(token); // 토큰 파싱 및 검증

			Claims body = claims.getBody();
			body.setExpiration(new Date()); // 만료 시간을 현재 시간으로 설정

			// 무효화된 토큰 생성
			String invalidatedToken = Jwts.builder()
					.setClaims(body)
					.signWith(SignatureAlgorithm.HS256, this.generateKey())
					.compact();

			System.out.println("Invalidated Token: " + invalidatedToken);
		} catch (JwtException e) {
			throw new BaseException(JWT_GET_USER_ERROR); // JWT 처리 중 예외 발생 시 예외 처리
		}
	}

	// 주어진 토큰에서 사용자 ID를 추출하여 반환
	public Long getUserId(String token) throws BaseException {
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parserBuilder()
					.setSigningKey(this.generateKey()) // 서명 키 설정
					.build()
					.parseClaimsJws(token); // 토큰 파싱 및 검증
		} catch (Exception e) {
			throw new BaseException(JWT_GET_USER_ERROR); // JWT 처리 중 예외 발생 시 예외 처리
		}
		Map<String, Object> value = claims.getBody();

		// 추출된 값이 Integer인 경우 Long으로 변환하여 반환
		Object userIdObject = value.get("userId");

		if (userIdObject instanceof Integer) {
			return ((Integer)userIdObject).longValue();
		} else if (userIdObject instanceof Long) {
			return (Long)userIdObject;
		} else {
			throw new BaseException(JWT_GET_USER_ERROR); // 예상치 못한 유형인 경우 예외 처리
		}
	}

	// 주어진 토큰의 만료 시간을 초 단위로 반환
	public Long getExpiration(String accessToken) {
		Date expiration = Jwts.parserBuilder()
				.setSigningKey(this.generateKey()) // 서명 키 설정
				.build()
				.parseClaimsJws(accessToken)
				.getBody()
				.getExpiration(); // 토큰 만료 시간 추출
		long now = new Date().getTime();
		return (expiration.getTime() - now) / 1000; // 만료 시간까지 남은 초 반환
	}

}
