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

	@Value("${jwt.salt}")
	private String salt;

	@Value("${jwt.access-token.expiretime}")
	private long accessTokenExpireTime;

	@Value("${jwt.refresh-token.expiretime}")
	private long refreshTokenExpireTime;

	public String createAccessToken(long userId) {
		return generateToken(userId, "access-token", accessTokenExpireTime);
	}

	public String createRefreshToken(long userId) {
		return generateToken(userId, "refresh-token", refreshTokenExpireTime);
	}

	private String generateToken(long userId, String subject, long expireTime) {
		Claims claims = Jwts.claims()
				.setSubject(subject)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expireTime));

		claims.put("userId", userId);

		String jwt = Jwts.builder()
				.setHeaderParam("typ", "JWT")
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS256, this.generateKey())
				.compact();

		return jwt;
	}

	// Signature 설정에 들어갈 key 생성.
	private byte[] generateKey() {
		byte[] key = null;
		try {
			key = salt.getBytes("UTF-8");
		} catch (Exception ignored) {

		}
		return key;
	}

	public boolean verifyToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder()
					.setSigningKey(this.generateKey())
					.build()
					.parseClaimsJws(token);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			return false;
		}
	}

	// TODO: 수정 필요 (작동 안 됨)
	public void logout(String token) throws BaseException {
		try {
			// 만료 시간을 현재 시간으로 설정하여 토큰을 무효화
			Jws<Claims> claims = Jwts.parserBuilder()
					.setSigningKey(this.generateKey())
					.build()
					.parseClaimsJws(token);

			Claims body = claims.getBody();
			body.setExpiration(new Date());

			String invalidatedToken = Jwts.builder()
					.setClaims(body)
					.signWith(SignatureAlgorithm.HS256, this.generateKey())
					.compact();

			System.out.println("Invalidated Token: " + invalidatedToken);
		} catch (JwtException e) {
			throw new BaseException(JWT_GET_USER_ERROR);
		}
	}

	// user index 반환
	public Long getUserId(String token) throws BaseException {
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parserBuilder()
					.setSigningKey(this.generateKey())
					.build()
					.parseClaimsJws(token);
		} catch (Exception e) {
			throw new BaseException(JWT_GET_USER_ERROR);
		}
		Map<String, Object> value = claims.getBody();

		Object userIdObject = value.get("userId");

		if (userIdObject instanceof Integer) {
			return ((Integer)userIdObject).longValue();
		} else if (userIdObject instanceof Long) {
			return (Long)userIdObject;
		} else {
			throw new BaseException(JWT_GET_USER_ERROR);
		}
	}

	// 토큰 만료기간 반환
	public Long getExpiration(String accessToken) {
		Date expiration = Jwts.parserBuilder()
				.setSigningKey(this.generateKey())
				.build()
				.parseClaimsJws(accessToken)
				.getBody()
				.getExpiration();
		long now = new Date().getTime();
		return (expiration.getTime() - now) / 1000;
	}

}
