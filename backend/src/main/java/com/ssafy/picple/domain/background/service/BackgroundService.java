package com.ssafy.picple.domain.background.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.domain.background.dto.response.BackgroundResponseDto;

public interface BackgroundService {

	// 기본 배경 사진 불러오기
	List<BackgroundResponseDto> getDefaultBackgrounds() throws BaseException;

	// 특정 사용자가 추가한 배경 사진 불러오기
	List<BackgroundResponseDto> getUserBackgrounds(Long userId) throws BaseException;

	// AI API를 사용해 prompt에 적힌 이미지 생성
	String createAIBackground(Long userId, String prompt) throws BaseException;

	// 로컬에 있는 사진을 배경 사진으로 추가
	void createLocalBackground(Long userId, MultipartFile file) throws BaseException;

	// 해당 사용자가 추가한 배경 사진 삭제
	void deleteBackground(Long backgroundId, Long userId) throws BaseException;

}
