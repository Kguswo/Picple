package com.ssafy.picple.domain.background.controller;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.domain.background.dto.request.CreateAIBackgroundRequest;
import com.ssafy.picple.domain.background.dto.request.DeleteBackgroundRequest;
import com.ssafy.picple.domain.background.dto.response.BackgroundResponseDto;
import com.ssafy.picple.domain.background.dto.response.ModifyBackgroundTitleResponse;
import com.ssafy.picple.domain.background.service.BackgroundService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/background")
public class BackgroundController {

	private final BackgroundService backgroundService;

	@GetMapping
	public BaseResponse<List<BackgroundResponseDto>> getDefaultBackgrounds() throws BaseException {
		try {
			List<BackgroundResponseDto> result = backgroundService.getDefaultBackgrounds();
			return new BaseResponse<>(result);
		} catch (Exception e) {
			throw new BaseException(DATABASE_ERROR);
		}
	}

	@GetMapping("/{userId}")
	public BaseResponse<List<BackgroundResponseDto>> getUserBackgrounds(@PathVariable Long userId) throws
			BaseException {
		try {
			List<BackgroundResponseDto> result = backgroundService.getUserBackgrounds(userId);
			return new BaseResponse<>(result);
		} catch (Exception e) {
			throw new BaseException(GET_USER_EMPTY);
		}
	}

	@PostMapping("/ai/{userId}")
	public BaseResponse<Object> createAiBackground(
			@PathVariable Long userId,
			@RequestBody CreateAIBackgroundRequest request) throws BaseException {

		backgroundService.createAIBackground(userId, request.getPrompt());

		return new BaseResponse<>(SUCCESS);
	}

	// 수정 필요
	@PostMapping("/local/{userId}")
	public BaseResponse<Object> createLocalBackground(
			@PathVariable Long userId,
			@RequestParam("file") MultipartFile file) throws BaseException {

		backgroundService.createLocalBackground(userId, file);
		
		return new BaseResponse<>(SUCCESS);
	}

	@DeleteMapping("/{backgroundId}")
	public BaseResponse<ModifyBackgroundTitleResponse> deleteBackground(
			@PathVariable Long backgroundId,
			@RequestBody DeleteBackgroundRequest request) throws BaseException {

		if (!request.isValidUserId()) {
			throw new BaseException(INVALID_USER_JWT);
		}

		try {
			backgroundService.deleteBackground(backgroundId, request.getUserId());
			return new BaseResponse<>(SUCCESS);
		} catch (Exception e) {
			throw new BaseException(DELETE_BACKGROUND_ERROR);
		}

	}
}
