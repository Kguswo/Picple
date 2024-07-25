package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.user.dto.request.LoginRequest;
import com.ssafy.picple.domain.user.dto.response.Token;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.repository.UserRepository;
import com.ssafy.picple.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;
import static org.apache.tomcat.util.net.openssl.ciphers.Encryption.AES256;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${password.encoding.key}")
    private String passwordKey;

    @Override
    @Transactional
    public User signUp(User user) throws BaseException {
        // check nickname duplicated
        if (userRepository.existsByNickname(user.getNickname())) {
            throw new BaseException(DUPLICATED_USER_NICKNAME);
        }
        try {
            user.setPasswordEncoding(encodePassword(user.getPassword()));
        } catch (Exception e) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Token login(LoginRequest loginRequest) throws BaseException {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        System.out.println(user.getPassword());
        System.out.println(encodePassword(loginRequest.getPassword()));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
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
    public void checkEmailDuplication(String email) throws BaseException {
        if (userRepository.existsByEmail(email)) {
            throw new BaseException(DUPLICATED_USER_EMAIL);
        }
    }

    /**
     * User 회원 탈퇴
     * @param userId
     * @return
     * @throws BaseException
     */
    @Override
    @Transactional
    public String deleteUser(Long userId) throws BaseException {
        if (userRepository.changeStatusOfDeleted(userId) == 1) {
            return "성공";
        } else {
            throw new BaseException(NOT_FOUND_USER);
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
