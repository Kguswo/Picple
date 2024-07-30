package com.ssafy.picple.domain.user.controller;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.user.dto.request.*;
import com.ssafy.picple.domain.user.dto.response.ModifyConfirmResponse;
import com.ssafy.picple.domain.user.dto.response.Token;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.service.EmailService;
import com.ssafy.picple.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    /**
     * 로그인
     * @param loginRequest
     * @return jwt tokens
     * @throws BaseException
     */
    @PostMapping("/login")
    public BaseResponse<Token> login(@RequestBody LoginRequest loginRequest) throws BaseException {
        return new BaseResponse<>(userService.login(loginRequest));
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

    /**
     * email 전송 (수정 필요 - PORT(587)가 열려있지 않음)
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
        return new BaseResponse<>(emailService.verifyEmailCode(emailCheckDto.getEmail(), emailCheckDto.getAuthNumber()));
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
        Long userId = (Long) request.getAttribute("userId");
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
        Long userId = (Long) request.getAttribute("userId");
        return new BaseResponse<>(userService.modifyUserPassword(userId, modifyPasswordRequest));
    }

    /**
     * 회원 탈퇴
     * @param
     * @return
     * @throws BaseException
     */
    @DeleteMapping("")
    public BaseResponse<String> deleteUser(HttpServletRequest request) throws BaseException {
        Long userId = (Long) request.getAttribute("userId");
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

}
