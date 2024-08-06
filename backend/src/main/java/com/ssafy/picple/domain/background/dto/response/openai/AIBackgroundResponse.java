package com.ssafy.picple.domain.background.dto.response.openai;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AIBackgroundResponse {
	long created;
	List<ImageObject> data;
}
