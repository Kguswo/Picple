package com.ssafy.picple.domain.background.dto.response;

import lombok.Data;

/**
 * 로컬 파일 업로드에 대한 응답 DTO
 */

@Data
public class UploadLocalFileResponse {
	private String url; // 업로드된 파일의 URL
}