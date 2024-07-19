package com.ssafy.picple.domain.boardlike.service;

public interface BoardLikeService {

	// 좋아요 누르기
	void likePhoto(Long boardId, Long userId);

	// 좋아요 취소
	void unlikePhoto(Long boardId, Long userId);

	// 좋아요 여부
	boolean isPhotoLikedByUser(Long boardId, Long userId);

}
