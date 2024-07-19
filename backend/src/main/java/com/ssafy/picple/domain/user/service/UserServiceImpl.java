package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
    public String signUp(User user) throws BaseException {
        // check email, nickname duplicated
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BaseException(DUPLICATED_USER_EMAIL);
        } else if (userRepository.existsByNickname(user.getNickname())) {
            throw new BaseException(DUPLICATED_USER_NICKNAME);
        }
        return null;
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

    /**
     * 닉네임 중복 체크
     * @param nickname
     * @return
     * @throws BaseException
     */
    @Override
    public String checkNicknameDuplication(String nickname) throws BaseException {
        if (!userRepository.existsByNickname(nickname)) {
            return "";
        } else {
            throw new BaseException(DUPLICATED_USER_NICKNAME);
        }
    }
}
