package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseResponse.BaseException;

public interface EmailService {
    String sendEmail(String email) throws BaseException;
    String verifyEmailCode(String email, String code) throws BaseException;
}
