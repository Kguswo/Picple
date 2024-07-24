package com.ssafy.picple.util;

import com.ssafy.picple.config.baseResponse.BaseException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

@Component
public class JWTUtil {

    @Value("${jwt.salt}")
    private String salt;

    @Value("${jwt.access-token.expiretime}")
    private long accessTokenExpireTime;

    @Value("${jwt.refresh-token.expiretime}")
    private long refreshTokenExpireTime;

    public String createAccessToken(Long userId) {
        return generateToken(userId, "access-token", accessTokenExpireTime);
    }

    public String createRefreshToken(Long userId) {
        return generateToken(userId, "refresh-token", refreshTokenExpireTime);
    }

    private String generateToken(Long userId, String subject, long expireTime) {
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

    public void logout(String token, Long expireTime) throws BaseException {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(this.generateKey())
                    .build()
                    .parseClaimsJws(token);

            Date expiration = claims.getBody().getExpiration();
            if (expiration.before(new Date())) {
                throw new BaseException(INVALID_JWT); // 사용자 정의 예외
            }
        } catch (JwtException e) {
            throw new BaseException(JWT_GET_USER_ERROR);
        }
    }

    // user index 반환
    public int getUserId(String authorization) throws BaseException {
        Jws<Claims> claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(this.generateKey())
                    .build()
                    .parseClaimsJws(authorization);
        } catch (Exception e) {
            throw new BaseException(JWT_GET_USER_ERROR);
        }
        Map<String, Object> value = claims.getBody();
        return (int) value.get("userId");
    }

}
