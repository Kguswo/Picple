package com.ssafy.picple.domain.boardlike.service;

import com.ssafy.picple.config.baseresponse.BaseException;

public interface BoardLikeService {

	// 좋아요 누르기
	void likePhoto(Long boardId, Long userId) throws BaseException;

	// 좋아요 취소
	void unlikePhoto(Long boardId, Long userId) throws BaseException;

	// 좋아요 상태 토글 (좋아요/취소)
	void toggleLike(Long boardId, Long userId) throws BaseException;

	// 좋아요 여부
	boolean isPhotoLikedByUser(Long boardId, Long userId);

}
