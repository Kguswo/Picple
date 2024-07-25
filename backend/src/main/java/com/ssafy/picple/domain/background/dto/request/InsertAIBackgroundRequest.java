package com.ssafy.picple.domain.background.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InsertAIBackgroundRequest {
	private String prompt;
}
