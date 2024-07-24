package com.ssafy.picple.domain.user.controller;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.user.dto.request.EmailCheckRequest;
import com.ssafy.picple.domain.user.dto.request.EmailRequest;
import com.ssafy.picple.domain.user.dto.response.UserListResponse;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.service.EmailService;
import com.ssafy.picple.domain.user.service.UserService;
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

    @GetMapping("/login")
    public BaseResponse<UserListResponse> login() throws BaseException {
        UserListResponse userDto = new UserListResponse();
        userDto.setUsers(userService.getUser());
        return new BaseResponse<>(userDto);
    }

    /**
     * 회원가입
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

    @PostMapping("/mail")
    public BaseResponse<String> mailSend(@RequestBody @Valid EmailRequest emailDto) throws BaseException {
        if (emailDto.getEmail() == null || emailDto.getEmail().trim().isEmpty()) {
            throw new BaseException(USER_EMAIL_EMPTY);
        }
        return new BaseResponse<>(emailService.sendEmail(emailDto.getEmail()));
    }

    @PostMapping("/mailcheck")
    public BaseResponse<String> mailCheck(@RequestBody @Valid EmailCheckRequest emailCheckDto) throws BaseException {
        if (emailCheckDto.getEmail() == null || emailCheckDto.getEmail().trim().isEmpty()) {
            throw new BaseException(USER_EMAIL_EMPTY);
        }
        return new BaseResponse<>(emailService.verifyEmailCode(emailCheckDto.getEmail(), emailCheckDto.getAuthNumber()));
    }

    @GetMapping("/sign-up/{email}")
    public BaseResponse<String> checkEmailDuplication(@PathVariable String email) throws BaseException {
        return new BaseResponse<>(userService.checkEmailDuplication(email));
    }

    @GetMapping("/sign-up/{nickname}")
    public BaseResponse<String> checkNicknameDuplication(@PathVariable String nickname) throws BaseException {
        return new BaseResponse<>(userService.checkNicknameDuplication(nickname));
    }

}
