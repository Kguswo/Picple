package com.ssafy.picple.domain.background.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLocalBackgroundRequest {
	private Long userId;
	private MultipartFile multipartFile;
}
