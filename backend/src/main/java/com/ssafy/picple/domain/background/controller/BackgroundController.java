package com.ssafy.picple.domain.background.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import com.ssafy.picple.domain.background.dto.BackgroundDto;
import com.ssafy.picple.domain.background.entity.Background;
import com.ssafy.picple.domain.background.repository.BackgroundRepository;
import com.ssafy.picple.domain.background.service.BackgroundService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/background")
public class BackgroundController {

	private final BackgroundService backgroundService;
	private final BackgroundRepository backgroundRepository;

	@GetMapping
	public BaseResponse<List<BackgroundDto>> getDefaultBackgrounds() throws BaseException {

		List<Background> backgrounds = backgroundService.getDefaultBackgrounds();

		List<BackgroundDto> collect = backgrounds.stream()
			.map(b -> new BackgroundDto(b.getId(), b.getBackgroundTitle(), b.getCreatedAt(), b.getIsDeleted()))
			.collect(Collectors.toList());

		System.out.println(collect);
		return new BaseResponse<>(collect);
	}

	@GetMapping("/{userId}")
	public BaseResponse<List<BackgroundDto>> getUserBackgrounds(@PathVariable Long userId) throws BaseException {

		List<Background> backgrounds = backgroundService.getUserBackgrounds(userId);

		List<BackgroundDto> collect = backgrounds.stream()
			.map(b -> new BackgroundDto(b.getId(), b.getBackgroundTitle(), b.getCreatedAt(), b.getIsDeleted()))
			.collect(Collectors.toList());

		System.out.println(collect);
		return new BaseResponse<>(collect);
	}

	@PostMapping("/ai/{userId}")
	public BaseResponse insertAiBackground(@PathVariable Long userId, @RequestBody String prompt) throws BaseException {

		Background background = backgroundService.insertAIBackground(userId, prompt);

		return new BaseResponse(
			new BackgroundDto(background.getId(), background.getBackgroundTitle(), background.getCreatedAt()));
	}

	@PostMapping("/local/{userId}")
	public BaseResponse insertLocalBackground(
		@PathVariable Long userId, @RequestParam("file") MultipartFile file) throws BaseException {

		Background background = backgroundService.insertLocalBackground(userId, file);

		return new BaseResponse(
			new BackgroundDto(background.getId(), background.getBackgroundTitle(), background.getCreatedAt()));
	}

	@DeleteMapping("/{backgroundId}")
	public BaseResponse deleteBackground(@PathVariable Long backgroundId, Long userId) throws BaseException {

		Background background = backgroundService.deleteBackground(backgroundId, userId);

		return new BaseResponse(
			new BackgroundDto(background.getId(), background.getBackgroundTitle(), background.getCreatedAt(),
				background.getIsDeleted()));
	}

}
