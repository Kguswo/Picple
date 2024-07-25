package com.ssafy.picple.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCheckRequest {
    @Email
    private String email;

    private String authNumber;
}
