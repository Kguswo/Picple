package com.ssafy.picple.domain.background.dto.response.openai;

import lombok.Data;

@Data
public class ImageObject {
	// String url; // URL 사용 시
	String base64Json; // Base64 사용 시
	String prompt;
}