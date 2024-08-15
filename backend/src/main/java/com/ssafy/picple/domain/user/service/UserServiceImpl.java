package com.ssafy.picple.domain.user.service;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponseStatus;
import com.ssafy.picple.domain.user.dto.request.LoginRequest;
import com.ssafy.picple.domain.user.dto.request.ModifyPasswordRequest;
import com.ssafy.picple.domain.user.dto.response.ModifyConfirmResponse;
import com.ssafy.picple.domain.user.dto.response.LoginResponse;
import com.ssafy.picple.domain.user.dto.response.UserInfoResponse;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.repository.UserRepository;
import com.ssafy.picple.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    /**
     * 로그인
     * @param loginRequest
     * @return
     * @throws BaseException
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) throws BaseException {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        if (!validatePassword(loginRequest.getPassword(), user.getPassword())) {
            throw new BaseException(INVALID_PASSWORD);
        }
        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);
        user.setRefreshTokenByLogin(refreshToken);
        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * 유저 정보 가져오기
     * @param userId
     * @return
     * @throws BaseException
     */
    @Override
    public UserInfoResponse getUserInfo(Long userId) throws BaseException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        return new UserInfoResponse(
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt().toLocalDate()
        );
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
     * 닉네임 수정
     * @param userId, nickname
     * @throws BaseException
     */
    @Override
    @Transactional
    public ModifyConfirmResponse modifyUserNickname(Long userId, String nickname) throws BaseException {
        System.out.println(nickname);
        if (userRepository.existsByNickname(nickname)) {
            throw new BaseException(DUPLICATED_USER_NICKNAME);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        try {
            user.modifyUserNickname(nickname);
            userRepository.save(user);
            return new ModifyConfirmResponse(user.getEmail(), nickname);
        } catch (Exception e) {
            throw new BaseException(ERROR_MODIFY_NICKNAME);
        }
    }

    /**
     * 비밀번호 변경
     * @param userId
     * @param modifyPassword
     * @return
     * @throws BaseException
     */
    @Override
    @Transactional
    public ModifyConfirmResponse modifyUserPassword(Long userId, ModifyPasswordRequest modifyPassword) throws BaseException {
        String oldPassword = modifyPassword.getOldPassword();
        String newPassword = modifyPassword.getNewPassword();
        if (newPassword.isEmpty() || oldPassword.isEmpty()) {
            throw new BaseException(EMPTY_REQUEST_PASSWORD);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        if (!validatePassword(oldPassword, user.getPassword())) {
            throw new BaseException(INVALID_PASSWORD);
        }
        try {
            user.setPasswordEncoding(encodePassword(newPassword));
            userRepository.save(user);
            return new ModifyConfirmResponse(user.getEmail(), user.getNickname());
        } catch (Exception e) {
            throw new BaseException(ERROR_MODIFY_PASSWORD);
        }
    }

    @Override
    @Transactional
    public BaseResponseStatus resetPassword(String email, String password) throws BaseException {
        if (password.isEmpty() || password.isEmpty()) {
            throw new BaseException(EMPTY_REQUEST_PASSWORD);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        try {
            user.setPasswordEncoding(encodePassword(password));
            userRepository.save(user);
            return BaseResponseStatus.SUCCESS;
        } catch (Exception e) {
            throw new BaseException(ERROR_MODIFY_PASSWORD);
        }
    }

    /**
     * User 회원 탈퇴
     * is_delete --> true
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

    /**
     * 비밀번호 암호화
     * @param password
     * @return
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 비밀번호 검증
     * 일치 : True
     * @param requestPassword
     * @param userPassword
     * @return
     */
    private Boolean validatePassword(String requestPassword, String userPassword) {
        return passwordEncoder.matches(requestPassword, userPassword);
    }

    /**
     * 액세스 토큰 재발급
     * @param
     * @return
     * @throws BaseException
     */
    public String refreshToken(String refreshToken) throws BaseException {
        Claims claims = jwtUtil.parseRefreshToken(refreshToken);

        User user = userRepository.findByEmail(claims.getSubject())
                .orElseThrow(() -> new BaseException(INVALID_REFRESH_TOKEN));

        if (user.getRefreshToken() == null) {
            new BaseException(INVALID_REFRESH_TOKEN);
        }

        String accessToken = jwtUtil.createAccessToken(user);
        return accessToken;
    }

    /**
     * 로그아웃
     * @param
     * @return
     * @throws BaseException
     */
    @Transactional
    public void logout(Long userId) throws BaseException{

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        user.deleteRefreshTokenByLogout();
    }
}
