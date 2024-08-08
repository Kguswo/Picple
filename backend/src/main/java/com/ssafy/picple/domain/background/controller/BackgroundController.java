package com.ssafy.picple.domain.background.controller;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

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

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.domain.background.dto.request.CreateAIBackgroundRequest;
import com.ssafy.picple.domain.background.dto.request.DeleteBackgroundRequest;
import com.ssafy.picple.domain.background.dto.response.BackgroundResponseDto;
import com.ssafy.picple.domain.background.dto.response.ModifyBackgroundTitleResponse;
import com.ssafy.picple.domain.background.service.BackgroundService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/backgrounds")
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
	public BaseResponse<List<BackgroundResponseDto>> getUserBackgrounds(
			HttpServletRequest request)
			throws BaseException {
		try {
			Long userId = (Long)request.getAttribute("userId");
			List<BackgroundResponseDto> result = backgroundService.getUserBackgrounds(userId);
			return new BaseResponse<>(result);
		} catch (Exception e) {
			throw new BaseException(GET_USER_EMPTY);
		}
	}

	// response 프론트에 넘겨줄지 의논 필요
	@PostMapping("/ai/{userId}")
	public BaseResponse<Object> createAiBackground(
			@PathVariable Long userId,
			@RequestBody CreateAIBackgroundRequest request) throws BaseException {

		backgroundService.createAIBackground(userId, request.getPrompt());

		return new BaseResponse<>(SUCCESS);
	}

	// response 프론트에 넘겨줄지 의논 필요
	@PostMapping("/local/{userId}")
	public BaseResponse<Object> createLocalBackground(
			@PathVariable Long userId,
			@RequestPart("file") MultipartFile file) throws BaseException {

		backgroundService.createLocalBackground(userId, file);

		return new BaseResponse<>(SUCCESS);
	}

	@DeleteMapping("/{backgroundId}")
	public BaseResponse<ModifyBackgroundTitleResponse> deleteBackground(
			HttpServletRequest request,
			@PathVariable Long backgroundId,
			@RequestBody DeleteBackgroundRequest deleteBackgroundRequest) throws BaseException {

		if (!deleteBackgroundRequest.isValidUserId()) {
			throw new BaseException(INVALID_USER_JWT);
		}

		try {
			Long userId = (Long)request.getAttribute("userId");
			backgroundService.deleteBackground(backgroundId, userId);
			return new BaseResponse<>(SUCCESS);
		} catch (Exception e) {
			throw new BaseException(DELETE_BACKGROUND_ERROR);
		}

	}
}
