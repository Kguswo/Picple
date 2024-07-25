package com.ssafy.picple.domain.photouser.service;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.photouser.dto.request.PhotoUserRequestDto;
import com.ssafy.picple.domain.photouser.dto.response.PhotoUserResponseDto;

public interface PhotoUserService {

	void updateContent(Long photoUserId, String newContent) throws BaseException;

	PhotoUserResponseDto createPhotoUser(PhotoUserRequestDto requestDto) throws BaseException;
}
