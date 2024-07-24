package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.user.dto.request.LoginRequest;
import com.ssafy.picple.domain.user.dto.response.Token;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.repository.UserRepository;
import com.ssafy.picple.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public List<User> getUser() throws BaseException {
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            return users;
        } else {
            throw new BaseException(GET_USER_EMPTY);
        }
    }

    @Override
    @Transactional
    public User signUp(User user) throws BaseException {
        // check nickname duplicated
        if (userRepository.existsByNickname(user.getNickname())) {
            throw new BaseException(DUPLICATED_USER_NICKNAME);
        }
        return userRepository.save(user);
    }

    @Override
    public Token login(LoginRequest loginRequest) throws BaseException {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new BaseException(INVALID_PASSWORD);
        }
        Token token = new Token();
        token.setAccessToken(jwtUtil.createAccessToken(user.getId()));
        return token;
    }

    /**
     * 이메일 중복 체크
     * @param email
     * @return
     * @throws BaseException
     */
    @Override
    public String checkEmailDuplication(String email) throws BaseException {
        if (!userRepository.existsByEmail(email)) {
            return "";
        } else {
            throw new BaseException(DUPLICATED_USER_EMAIL);
        }
    }

    @Override
    @Transactional
    public String deleteUser(Long userId) throws BaseException {
        if (userRepository.changeStatusOfDeleted(userId) == 1) {
            return "성공";
        } else {
            throw new BaseException(NOT_FOUND_USER);
        }
    }
}
