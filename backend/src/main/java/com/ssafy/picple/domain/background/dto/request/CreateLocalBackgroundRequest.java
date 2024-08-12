package com.ssafy.picple.domain.background.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * 로컬 배경 사진 생성 요청 DTO
 */

@Data
public class CreateLocalBackgroundRequest {
	private Long userId; // 사용자 ID
	private MultipartFile multipartFile; // 사용자가 올린 파일
}
