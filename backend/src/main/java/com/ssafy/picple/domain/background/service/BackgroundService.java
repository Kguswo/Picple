package com.ssafy.picple.domain.background.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.background.entity.Background;

public interface BackgroundService {

	// 기본 배경 사진 불러오기
	List<Background> getDefaultBackgrounds();

	// 해당 사용자가 추가한 배경 사진 불러오기
	List<Background> getUserBackgrounds(Long userId);

	// AI API를 사용해 prompt에 적힌 이미지 생성
	Background insertAIBackground(Long userId, String prompt);

	// 로컬에 있는 사진을 배경 사진으로 추가
	Background insertLocalBackground(Long userId, MultipartFile file);

	// 해당 사용자가 추가한 배경 사진 삭제
	Background deleteBackground(Long backgroundId, Long userId) throws BaseException;
}
