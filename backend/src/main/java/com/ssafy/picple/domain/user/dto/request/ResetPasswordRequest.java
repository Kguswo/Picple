package com.ssafy.picple.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordRequest {
    @Email
    private String email;
    private String password;
}
