package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.user.entity.User;

import java.util.List;

public interface UserService {
    List<User> getUser() throws BaseException;
}
