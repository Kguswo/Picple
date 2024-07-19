package com.ssafy.picple.domain.boardlike.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.boardlike.service.BoardLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class BoardLikeController {

	private final BoardLikeService likeService;

	@GetMapping("/isLiked")
	public BaseResponse<Boolean> isPhotoLikedByUser(@RequestParam Long boardId, @RequestParam Long userId) {
		boolean isLiked = likeService.isPhotoLikedByUser(boardId, userId);
		return new BaseResponse<>(isLiked);
	}

	@PatchMapping("/like/{boardId}")
	public BaseResponse<?> likePhoto(@PathVariable Long boardId, @RequestParam Long userId) {
		try {
			likeService.likePhoto(boardId, userId);
			return new BaseResponse<>(BaseResponseStatus.SUCCESS);
		} catch (Exception e) {
			return new BaseResponse<>(BaseResponseStatus.RESPONSE_ERROR);
		}
	}

	@PatchMapping("/likeCancel/{boardId}")
	public BaseResponse<?> unlikePhoto(@PathVariable Long boardId, @RequestParam Long userId) {
		try {
			likeService.unlikePhoto(boardId, userId);
			return new BaseResponse<>(BaseResponseStatus.SUCCESS);
		} catch (Exception e) {
			return new BaseResponse<>(BaseResponseStatus.RESPONSE_ERROR);
		}
	}
}
