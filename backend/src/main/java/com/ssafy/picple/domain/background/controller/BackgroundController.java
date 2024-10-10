package com.ssafy.picple.domain.background.controller;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponse;
import com.ssafy.picple.domain.background.dto.request.CreateAIBackgroundRequest;
import com.ssafy.picple.domain.background.dto.request.DeleteBackgroundRequest;
import com.ssafy.picple.domain.background.dto.response.BackgroundResponseDto;
import com.ssafy.picple.domain.background.service.BackgroundService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/backgrounds")
public class BackgroundController {

	private final BackgroundService backgroundService;

	// 기본 배경 사진들을 가져옴
	@GetMapping
	public BaseResponse<List<BackgroundResponseDto>> getDefaultBackgrounds() throws BaseException {

		List<BackgroundResponseDto> result = backgroundService.getDefaultBackgrounds();

		return new BaseResponse<>(result);
	}

	// 사용자가 등록한 배경 사진을 가져옴
	@GetMapping("/user")
	public BaseResponse<List<BackgroundResponseDto>> getUserBackgrounds(
			HttpServletRequest request)
			throws BaseException {

		Long userId = (Long)request.getAttribute("userId");
		List<BackgroundResponseDto> result = backgroundService.getUserBackgrounds(userId);

		return new BaseResponse<>(result);
	}

	// 생성형 AI를 통해 배경 사진을 만듦
	@PostMapping("/ai")
	public BaseResponse<Object> createAiBackground(
			HttpServletRequest request,
			@RequestBody CreateAIBackgroundRequest aiBackgroundRequest) throws BaseException {

		Long userId = (Long)request.getAttribute("userId");
		String url = backgroundService.createAIBackground(userId, aiBackgroundRequest.getPrompt());

		return new BaseResponse<>(url);
	}

	// 로컬에서 사진을 불러와 배경 사진을 등록
	@PostMapping("/local/{userId}")
	public BaseResponse<Object> createLocalBackground(
			@PathVariable Long userId,
			@RequestPart("file") MultipartFile file) throws BaseException {

		backgroundService.createLocalBackground(userId, file);

		return new BaseResponse<>(SUCCESS);
	}

	// 사용자의 배경 사진 삭제
	@DeleteMapping("/{backgroundId}")
	public BaseResponse<Object> deleteBackground(
			HttpServletRequest request,
			@PathVariable Long backgroundId,
			@RequestBody DeleteBackgroundRequest deleteBackgroundRequest) throws BaseException {

		if (!deleteBackgroundRequest.isValidUserId()) {
			throw new BaseException(INVALID_USER_JWT);
		}

		Long userId = (Long)request.getAttribute("userId");
		backgroundService.deleteBackground(backgroundId, userId);
		return new BaseResponse<>(SUCCESS);
	}
}
