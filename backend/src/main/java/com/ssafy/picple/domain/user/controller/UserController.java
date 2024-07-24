package com.ssafy.picple.domain.user.controller;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.user.dto.request.EmailCheckRequest;
import com.ssafy.picple.domain.user.dto.request.EmailRequest;
import com.ssafy.picple.domain.user.dto.request.LoginRequest;
import com.ssafy.picple.domain.user.dto.response.Token;
import com.ssafy.picple.domain.user.dto.response.UserListResponse;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.service.EmailService;
import com.ssafy.picple.domain.user.service.UserService;
import com.ssafy.picple.util.JWTUtil;
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
    private final JWTUtil jwtUtil;

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
            System.out.println("여기서 에러 발생");
            return new BaseResponse<>(FAILED_USER_SIGNUP);
        }
    }

    /**
     * email 전송 (수정 필요)
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
     * 회원 탈퇴
     * @param user
     * @return
     * @throws BaseException
     */
    @DeleteMapping("")
    public BaseResponse<String> deleteUser(@RequestBody User user) throws BaseException {
        return new BaseResponse<>(userService.deleteUser(user.getId()));
    }

}
