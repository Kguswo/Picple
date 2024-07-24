package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.user.dto.request.LoginRequest;
import com.ssafy.picple.domain.user.dto.response.Token;
import com.ssafy.picple.domain.user.entity.User;

import java.util.List;

public interface UserService {
    List<User> getUser() throws BaseException;
    User signUp(User user) throws BaseException;
    Token login(LoginRequest loginRequest) throws BaseException;

    String checkEmailDuplication(String email) throws BaseException;

    String deleteUser(Long userId) throws BaseException;
}
