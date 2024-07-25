package com.ssafy.picple.domain.user.dto.response;

import com.ssafy.picple.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserListResponse {
    private List<User> users;
}
