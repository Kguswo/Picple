package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.user.dto.request.LoginRequest;
import com.ssafy.picple.domain.user.dto.request.ModifyPasswordRequest;
import com.ssafy.picple.domain.user.dto.response.ModifyConfirmResponse;
import com.ssafy.picple.domain.user.dto.response.Token;
import com.ssafy.picple.domain.user.entity.User;

public interface UserService {
    User signUp(User user) throws BaseException;

    Token login(LoginRequest loginRequest) throws BaseException;

    void checkEmailDuplication(String email) throws BaseException;

    // 회원 정보 수정
    ModifyConfirmResponse modifyUserNickname(Long userId, String nickname) throws BaseException;
    ModifyConfirmResponse modifyUserPassword(Long userId, ModifyPasswordRequest modifyPasswordRequest) throws BaseException;

    String deleteUser(Long userId) throws BaseException;
}
