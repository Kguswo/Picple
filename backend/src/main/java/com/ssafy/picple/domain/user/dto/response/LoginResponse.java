package com.ssafy.picple.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 로그인 응답을 위한 DTO
 * 로그인 성공 시 클라이언트에게 반환되는 데이터를 담고 있음
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
}
