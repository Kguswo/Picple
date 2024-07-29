package com.ssafy.picple.domain.boardlike.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.boardlike.service.BoardLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class BoardLikeController {

	private final BoardLikeService likeService;

	/**
	 * 좋아요 및 숫자 1 증가
	 *
	 * @param boardId
	 * @param userId
	 * @return
	 */
	@GetMapping("/{boardId}")
	public BaseResponse<Boolean> isPhotoLikedByUser(@PathVariable Long boardId,
			@RequestParam Long userId) throws BaseException {
		boolean isLiked = likeService.isPhotoLikedByUser(boardId, userId);
		return new BaseResponse<>(isLiked);
	}

	/**
	 * 좋아요 취소 및 숫자 1 감소
	 *
	 * @param boardId
	 * @param userId
	 * @return
	 */
	@PatchMapping("/{boardId}")
	public BaseResponse<?> changeIsLiked(@PathVariable Long boardId,
			@RequestParam(required = false) Long userId) throws BaseException {
		if (userId == null) {
			return new BaseResponse<>(BaseResponseStatus.GET_USER_EMPTY);
		}
		try {
			boolean isLiked = likeService.isPhotoLikedByUser(boardId, userId);
			if (isLiked) {
				likeService.unlikePhoto(boardId, userId);
			} else {
				likeService.likePhoto(boardId, userId);
			}
			return new BaseResponse<>(BaseResponseStatus.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new BaseResponse<>(BaseResponseStatus.RESPONSE_ERROR);
		}
	}

}
