package com.ssafy.picple.domain.photouser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.domain.photouser.dto.request.PhotoUserRequestDto;
import com.ssafy.picple.domain.photouser.dto.response.PhotoUserResponseDto;
import com.ssafy.picple.domain.photouser.service.PhotoUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photo_user")
public class PhotoUserController {

	private final PhotoUserService photoUserService;

	@GetMapping("/{photoUserId}")
	public BaseResponse<PhotoUserResponseDto> getPhotoUserContent(@RequestParam PhotoUserRequestDto requestDto) throws
			BaseException {
		PhotoUserResponseDto responseDto = photoUserService.createPhotoUser(requestDto);
		return new BaseResponse<>(responseDto);
	}

	@PostMapping
	public BaseResponse<PhotoUserResponseDto> createPhotoUser(@RequestBody PhotoUserRequestDto requestDto) throws
			BaseException {
		PhotoUserResponseDto responseDto = photoUserService.createPhotoUser(requestDto);
		return new BaseResponse<>(responseDto);
	}
}