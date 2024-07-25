package com.ssafy.picple.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    @Email(message = "올바른 형식의 이메일을 입력해주세요.")
    private String email;
}
