package com.ssafy.picple.domain.background.dto.response.openai;

import lombok.Data;

@Data
public class ImageObject {
	// String b64_json;
	String url;
	String revised_prompt;
}