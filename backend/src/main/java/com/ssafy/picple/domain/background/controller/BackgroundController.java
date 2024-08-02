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
import com.ssafy.picple.domain.background.dto.request.DeleteBackgroundRequest;
import com.ssafy.picple.domain.background.dto.request.InsertAIBackgroundRequest;
import com.ssafy.picple.domain.background.dto.response.BackgroundResponseDto;
import com.ssafy.picple.domain.background.dto.response.InsertBackgroundResponse;
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

	@PostMapping("/ai/{userId}")
	public BaseResponse<InsertBackgroundResponse> insertAiBackground(
			HttpServletRequest request,
			@RequestBody InsertAIBackgroundRequest aiBackgroundRequest) throws BaseException {
		try {
			Long userId = (Long)request.getAttribute("userId");
			backgroundService.insertAIBackground(userId, aiBackgroundRequest.getPrompt());
			return new BaseResponse<>(SUCCESS);
		} catch (Exception e) {
			throw new BaseException(AI_BACKGROUND_GENERATION_ERROR);
		}
	}

	@PostMapping("/local/{userId}")
	public BaseResponse<InsertBackgroundResponse> insertLocalBackground(
			HttpServletRequest request,
			@RequestParam("file") MultipartFile file) throws BaseException {
		try {
			Long userId = (Long)request.getAttribute("userId");
			backgroundService.insertLocalBackground(userId, file);
			return new BaseResponse<>(SUCCESS);
		} catch (Exception e) {
			throw new BaseException(LOCAL_BACKGROUND_UPLOAD_ERROR);
		}
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
