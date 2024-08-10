package com.ssafy.picple.domain.background.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로컬 배경 사진 생성 요청 DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLocalBackgroundRequest {
	private Long userId; // 사용자 ID
	private MultipartFile multipartFile; // 사용자가 올린 파일
}
