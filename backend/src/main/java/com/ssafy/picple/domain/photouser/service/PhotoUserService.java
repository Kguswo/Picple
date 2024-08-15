package com.ssafy.picple.domain.photouser.service;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.domain.photouser.dto.request.PhotoUserRequestDto;
import com.ssafy.picple.domain.photouser.dto.response.PhotoUserResponseDto;

public interface PhotoUserService {

	// PhotoUser의 content 불러오기
	PhotoUserResponseDto getPhotoUserContent(PhotoUserRequestDto requestDto, Long userId);

	// PhotoUser 생성
	PhotoUserResponseDto createPhotoUser(PhotoUserRequestDto requestDto, Long userId) throws BaseException;
}
