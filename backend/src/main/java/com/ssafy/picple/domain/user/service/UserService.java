package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.user.entity.User;

import java.util.List;

public interface UserService {
    List<User> getUser() throws BaseException;
    Boolean signUp(User user) throws BaseException;

    String checkEmailDuplication(String email) throws BaseException;
    String checkNicknameDuplication(String nickname) throws BaseException;
}
