package com.ssafy.picple.domain.boardlike.controller;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponse;
import com.ssafy.picple.config.baseresponse.BaseResponseStatus;
import com.ssafy.picple.domain.board.service.BoardService;
import com.ssafy.picple.domain.boardlike.service.BoardLikeService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class BoardLikeController {

	private final BoardLikeService likeService;

	/**
	 * 좋아요 여부 체크
	 *
	 * @param request
	 * @param boardId
	 * @return
	 * @throws BaseException
	 */
	@GetMapping("/{boardId}")
	public BaseResponse<Boolean> isPhotoLikedByUser(HttpServletRequest request,
			@PathVariable Long boardId) throws BaseException {

		Long userId = (Long)request.getAttribute("userId");
		boolean isLiked = likeService.isPhotoLikedByUser(boardId, userId);

		return new BaseResponse<>(isLiked);
	}

	/**
	 * 좋아요 시 : 좋아요 및 숫자 1 증가
	 * 좋아요 취소시 : 좋아요 취소 및 숫자 1 감소
	 *
	 * @param request
	 * @param boardId
	 * @return
	 * @throws BaseException
	 */
	@PatchMapping("/{boardId}")
	public BaseResponse<?> changeIsLiked(HttpServletRequest request,
			@PathVariable Long boardId) throws BaseException {

		Long userId = (Long) request.getAttribute("userId");
		likeService.toggleLike(boardId, userId);

		return new BaseResponse<>(SUCCESS);
	}

}
