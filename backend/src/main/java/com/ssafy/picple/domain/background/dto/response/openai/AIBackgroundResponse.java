package com.ssafy.picple.domain.background.dto.response.openai;

import java.util.List;

import lombok.Data;

@Data
public class AIBackgroundResponse {
	long created;
	List<ImageObject> data;
}
