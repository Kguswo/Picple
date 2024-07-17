package com.ssafy.picple.util;

import com.ssafy.picple.config.baseResponse.BaseException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {
    public class JWTUtil {

        @Value("${jwt.salt}")
        private String salt;

        @Value("${jwt.access-token.expiretime}")
        private long accessTokenExpireTime;

        @Value("${jwt.refresh-token.expiretime}")
        private long refreshTokenExpireTime;

        public String createAccessToken(int userId) {
            return generateToken(userId, "access-token", accessTokenExpireTime);
        }

        //	AccessToken에 비해 유효기간을 길게 설정.
        public String createRefreshToken(int userId) {
            return generateToken(userId, "refresh-token", refreshTokenExpireTime);
        }

        private String generateToken(int userId, String subject, long expireTime) {
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
                // charset 설정 안하면 사용자 플랫폼의 기본 인코딩 설정으로 인코딩 됨.
                key = salt.getBytes("UTF-8");
            } catch (Exception e) {
                System.out.println("test error");
            }
            return key;
        }

        //	전달 받은 토큰이 제대로 생성된 것인지 확인 하고 문제가 있다면 UnauthorizedException 발생.
        public boolean verifyToken(String token) {
            try {
                // Json Web Signature? 서버에서 인증을 근거로 인증 정보를 서버의 private key 서명 한것을 토큰화 한것
                // setSigningKey : JWS 서명 검증을 위한  secret key 세팅
                // parseClaimsJws : 파싱하여 원본 jws 만들기
                Jws<Claims> claims = Jwts.parser()
                        .setSigningKey(this.generateKey())
                        .parseClaimsJws(token);
                return !claims.getBody().getExpiration().before(new Date());
            } catch (ExpiredJwtException e) {
                return false;
            }
        }

        public Long getExpiration(String accessToken) {
            Date expiration = Jwts.parser()
                    .setSigningKey(this.generateKey())
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getExpiration();
            Long now = new Date().getTime();
            return (expiration.getTime() - now) / 1000;
        }

        public int getUserId(String authorization) throws BaseException {
            Jws<Claims> claims = null;
            try {
                claims = Jwts.parser().setSigningKey(this.generateKey()).parseClaimsJws(authorization);
            } catch (Exception e) {
                throw new UnAuthorizedException();
            }
            Map<String, Object> value = claims.getBody();
            return (int) value.get("userIdx");
        }
    }
}
