package com.ssafy.picple.util;

import com.ssafy.picple.domain.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    private final byte[] accessSecret;


    private final byte[] refreshSecret;

    @Value("${jwt.accessExpire}")
    private Long accessTokenExpireTime;

    @Value("${jwt.refreshExpire}")
    private Long refreshTokenExpireTime;

    public JWTUtil(@Value("${jwt.accessSecret}") String accessSecret, @Value("${jwt.refreshSecret}") String refreshSecret) {
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
    }

    public String createAccessToken(User user) {
        return generateToken(user, accessTokenExpireTime, accessSecret);
    }

    public String createRefreshToken(User user) {
        return generateToken(user, refreshTokenExpireTime, refreshSecret);
    }

    public String generateToken(User user, long expireTime, byte[] secretKey) {

        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("userId", user.getId());
        claims.put("nickname", user.getNickname());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date().getTime() + expireTime)))
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    public long getUserIdByToken(String accessToken) {
        Claims claims = parseAccessToken(accessToken);
        return (long)((int)claims.get("userId"));
    }

    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    public Claims parseToken(String token, byte[] secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }

    public boolean verifyAccessToken(String token) {
        try {
            Claims claims = parseToken(token, accessSecret);
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public boolean verifyRefreshToken(String token) {
        try {
            Claims claims = parseToken(token, refreshSecret);
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }
}
