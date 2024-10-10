package com.ssafy.picple.domain.photouser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponse;
import com.ssafy.picple.domain.photouser.dto.request.PhotoUserRequestDto;
import com.ssafy.picple.domain.photouser.dto.response.PhotoUserResponseDto;
import com.ssafy.picple.domain.photouser.service.PhotoUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photo_users")
public class PhotoUserController {

	private final PhotoUserService photoUserService;

	// 사용자의 사진 정보(메모)를 가져옴,'PhotoUserRequestDto' 기반 사용자 사진 정보 조회
	@GetMapping("/{photoUserId}")
	public BaseResponse<PhotoUserResponseDto> getPhotoUserContent(
			HttpServletRequest request,
			@RequestParam PhotoUserRequestDto requestDto) {

		Long userId = (Long)request.getAttribute("userId");
		return new BaseResponse<>(photoUserService.getPhotoUserContent(requestDto, userId));

	}

	// 'PhotoUserRequestDto' 기반 새로운 PhotoUser 생성
	@PostMapping
	public BaseResponse<PhotoUserResponseDto> createPhotoUser(
			HttpServletRequest request,
			@RequestBody PhotoUserRequestDto requestDto)
			throws BaseException {

		Long userId = (Long)request.getAttribute("userId");
		return new BaseResponse<>(photoUserService.createPhotoUser(requestDto, userId));

	}
}