package com.ssafy.picple.domain.background.dto.response.openai;

import java.util.List;

import lombok.Data;

/**
 * OpenAI API 형식과 일치하도록 작성하였음
 */

@Data
public class AIBackgroundResponse {
	long created;
	List<ImageObject> data;
}
