package com.ssafy.picple.domain.user.controller;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.domain.user.dto.response.UserListResponse;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * 테스트 코드 (User 목록 불러오기 - Dto 사용)
     * @return
     * @throws BaseException
     */
    @GetMapping("/test1")
    public BaseResponse<UserListResponse> getUserDto() throws BaseException {
        UserListResponse userDto = new UserListResponse();
        userDto.setUsers(userService.getUser());
        return new BaseResponse<>(userDto);
    }

    /**
     * 테스트 코드 (User 목록 불러오기)
     * @return
     * @throws BaseException
     */
    @GetMapping("/test2")
    public BaseResponse<List<User>> getUser() throws BaseException {
        return new BaseResponse<>(userService.getUser());
    }
}
