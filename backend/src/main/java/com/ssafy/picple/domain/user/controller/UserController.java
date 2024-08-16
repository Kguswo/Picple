package com.ssafy.picple.domain.user.controller;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import com.ssafy.picple.domain.user.dto.request.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponse;
import com.ssafy.picple.config.baseresponse.BaseResponseStatus;
import com.ssafy.picple.domain.user.dto.response.LoginResponse;
import com.ssafy.picple.domain.user.dto.response.ModifyConfirmResponse;
import com.ssafy.picple.domain.user.dto.response.UserInfoResponse;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.service.EmailService;
import com.ssafy.picple.domain.user.service.UserService;
import com.ssafy.picple.util.JWTUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	private final EmailService emailService;
	private final JWTUtil jwtUtil;

    @Value("${server.servlet.session.cookie.domain}")
    private String domain;

    @Value("${server.servlet.session.cookie.path}")
    private String path;

    @Value("${server.servlet.session.cookie.max-age}")
    private Integer maxAge;

    /**
     * 로그인
     * @param loginRequest
     * @return jwt tokens
     * @throws BaseException
     */
    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            LoginResponse loginResponse = userService.login(loginRequest);
            Cookie cookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
            cookie.setDomain(domain);
            cookie.setPath(path);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);
            return new BaseResponse<>(loginResponse.getAccessToken());
        } catch (BaseException e) {
            return new BaseResponse<>(NOT_FOUND_USER);
        }
    }

	/**
	 * 회원 가입
	 * @param user
	 * @return
	 * @throws BaseException
	 */
	@PostMapping("/sign-up")
	public BaseResponse<BaseResponseStatus> signUp(@RequestBody User user) throws BaseException {
		if (userService.signUp(user) != null) {
			return new BaseResponse<>(SUCCESS);
		} else {
			return new BaseResponse<>(FAILED_USER_SIGNUP);
		}
	}

	@GetMapping("/info")
	public BaseResponse<UserInfoResponse> getUserInfo(HttpServletRequest request) throws BaseException {
		Long userId = (Long)request.getAttribute("userId");
		return new BaseResponse<>(userService.getUserInfo(userId));
	}

    /**
     * email 전송 (회원가입용)
     * @param emailDto
     * @return
     * @throws BaseException
     */
    @PostMapping("/mail")
    public BaseResponse<String> mailSend(@RequestBody @Valid EmailRequest emailDto) throws BaseException {
        if (emailDto.getEmail() == null || emailDto.getEmail().trim().isEmpty()) {
            throw new BaseException(USER_EMAIL_EMPTY);
        }
        userService.checkEmailDuplication(emailDto.getEmail());
        return new BaseResponse<>(emailService.sendEmail(emailDto.getEmail()));
    }

    /**
     * email 전송 (비밀번호 찾기용)
     * @param emailDto
     * @return
     * @throws BaseException
     */
    @PostMapping("/mail/find")
    public BaseResponse<String> mailSendByFind(@RequestBody @Valid EmailRequest emailDto) throws BaseException {
        if (emailDto.getEmail() == null || emailDto.getEmail().trim().isEmpty()) {
            throw new BaseException(USER_EMAIL_EMPTY);
        }
        return new BaseResponse<>(emailService.sendEmail(emailDto.getEmail()));
    }

	/**
	 * email 전송 체크
	 * @param emailCheckDto
	 * @return
	 * @throws BaseException
	 */
	@PostMapping("/mailcheck")
	public BaseResponse<String> mailCheck(@RequestBody @Valid EmailCheckRequest emailCheckDto) throws BaseException {
		if (emailCheckDto.getEmail() == null || emailCheckDto.getEmail().trim().isEmpty()) {
			throw new BaseException(USER_EMAIL_EMPTY);
		}
		return new BaseResponse<>(
				emailService.verifyEmailCode(emailCheckDto.getEmail(), emailCheckDto.getAuthNumber()));
	}

	/**
	 * 닉네임 수정
	 * @param request
	 * @param modifyNicknameRequest
	 * @return
	 * @throws BaseException
	 */
	@PatchMapping("/modify/nickname")
	public BaseResponse<ModifyConfirmResponse> modifyUserNickname(HttpServletRequest request,
			@RequestBody ModifyNicknameRequest modifyNicknameRequest) throws BaseException {
		Long userId = (Long)request.getAttribute("userId");
		return new BaseResponse<>(userService.modifyUserNickname(userId, modifyNicknameRequest.getNickname()));
	}

	/**
	 * 비밀번호 변경
	 * @param request
	 * @param modifyPasswordRequest
	 * @return
	 * @throws BaseException
	 */
	@PatchMapping("/modify/password")
	public BaseResponse<ModifyConfirmResponse> modifyUserPassword(HttpServletRequest request,
			@RequestBody ModifyPasswordRequest modifyPasswordRequest) throws BaseException {
		Long userId = (Long)request.getAttribute("userId");
		return new BaseResponse<>(userService.modifyUserPassword(userId, modifyPasswordRequest));
	}

	/**
	 * Password 재설정 (비밀번호 찾기)
	 * @param resetPasswordRequest
	 * @return
	 * @throws BaseException
	 */
	@PatchMapping("/reset-password")
	public BaseResponse<BaseResponseStatus> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws BaseException {
		return new BaseResponse<>(userService.resetPassword(
				resetPasswordRequest.getEmail(),
				resetPasswordRequest.getPassword()
		));
	}

	/**
     * 로그아웃
     * @param request, response
     * @return BaseResponse
     * @throws BaseException
     */
    @PostMapping("/logout")
    public BaseResponse<BaseResponseStatus> logout(HttpServletRequest request, HttpServletResponse response) throws BaseException {

        try {
            Long userId = (Long)request.getAttribute("userId");
            userService.logout(userId);
        } catch (BaseException e) {
            throw new BaseException(NOT_FOUND_USER);
        }

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
		cookie.setDomain(domain);
		cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

		return new BaseResponse<>(SUCCESS);
	}

	/**
	 * 회원 탈퇴
	 * @param
	 * @return
	 * @throws BaseException
	 */
	@DeleteMapping("")
	public BaseResponse<String> deleteUser(HttpServletRequest request) throws BaseException {
		Long userId = (Long)request.getAttribute("userId");
		return new BaseResponse<>(userService.deleteUser(userId));
	}

	/**
	 * 테스트용 이메일 인증
	 * @param emailRequest
	 * @return
	 * @throws BaseException
	 */
	@PostMapping("/test-email")
	public BaseResponse<?> checkEmailTemp(@RequestBody @Valid EmailRequest emailRequest) throws BaseException {
		if (emailRequest.getEmail() == null || emailRequest.getEmail().trim().isEmpty()) {
			throw new BaseException(USER_EMAIL_EMPTY);
		}
		userService.checkEmailDuplication(emailRequest.getEmail());
		return new BaseResponse<>(SUCCESS);
	}

    /**
     * 토큰 재발급
     * @param request
     * @return jwt tokens
     * @throws BaseException
     */
    @PostMapping("/refresh-token")
    public BaseResponse<?> refreshToken(HttpServletRequest request) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        if (refreshToken == null || !jwtUtil.verifyRefreshToken(refreshToken)) {
            return new BaseResponse<>(INVALID_REFRESH_TOKEN);
        }

        try {
            String accessToken = userService.refreshToken(refreshToken);
            return new BaseResponse<>(accessToken);
        } catch(BaseException e) {
            return new BaseResponse<>(INVALID_REFRESH_TOKEN);
        }
    }
}
