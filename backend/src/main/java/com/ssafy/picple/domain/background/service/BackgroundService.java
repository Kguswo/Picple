package com.ssafy.picple.domain.background.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.background.dto.response.BackgroundResponseDto;

public interface BackgroundService {

	// 기본 배경 사진 불러오기
	List<BackgroundResponseDto> getDefaultBackgrounds() throws BaseException;

	// 해당 사용자가 추가한 배경 사진 불러오기
	Optional<List<BackgroundResponseDto>> getUserBackgrounds(Long userId);

	// AI API를 사용해 prompt에 적힌 이미지 생성
	BackgroundResponseDto insertAIBackground(Long userId, String prompt) throws BaseException;

	// 로컬에 있는 사진을 배경 사진으로 추가
	BackgroundResponseDto insertLocalBackground(Long userId, MultipartFile file) throws BaseException;

	// 해당 사용자가 추가한 배경 사진 삭제
	BackgroundResponseDto deleteBackground(Long backgroundId, Long userId) throws BaseException;

}
