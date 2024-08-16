package com.ssafy.picple.domain.background.dto.request;

import lombok.Data;

/**
 * 배경 사진 제목 수정 요청 DTO
 */

@Data
public class ModifyBackgroundTitleRequest {
	private String newTitle; // 사용자가 새로 작성한 배경 사진 제목
}