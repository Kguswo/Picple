package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponseStatus;
import com.ssafy.picple.domain.user.dto.request.LoginRequest;
import com.ssafy.picple.domain.user.dto.request.ModifyPasswordRequest;
import com.ssafy.picple.domain.user.dto.response.LoginResponse;
import com.ssafy.picple.domain.user.dto.response.ModifyConfirmResponse;
import com.ssafy.picple.domain.user.dto.response.UserInfoResponse;
import com.ssafy.picple.domain.user.entity.User;

public interface UserService {
    User signUp(User user) throws BaseException;

    LoginResponse login(LoginRequest loginRequest) throws BaseException;

    UserInfoResponse getUserInfo(Long userId) throws BaseException;

    // 이메일 중복 여부 확인
    void checkEmailDuplication(String email) throws BaseException;

    // 회원 정보 수정
    ModifyConfirmResponse modifyUserNickname(Long userId, String nickname) throws BaseException;
    ModifyConfirmResponse modifyUserPassword(Long userId, ModifyPasswordRequest modifyPasswordRequest) throws BaseException;
    BaseResponseStatus resetPassword(String email, String password) throws BaseException;

    // 회원 탈퇴
    String deleteUser(Long userId) throws BaseException;

    void logout(Long userId) throws BaseException;

    String refreshToken(String refreshToken) throws BaseException;
}
