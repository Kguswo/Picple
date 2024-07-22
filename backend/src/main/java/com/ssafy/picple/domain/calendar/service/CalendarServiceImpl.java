package com.ssafy.picple.domain.calendar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.domain.board.repository.BoardRepository;
import com.ssafy.picple.domain.calendar.repository.CalendarRepository;
import com.ssafy.picple.domain.photo.repository.PhotoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarServiceImpl {

	private final CalendarRepository calendarRepository;
	private final BoardRepository boardRepository;
	private final PhotoRepository photoRepository;

	// @Override
	// public void sharePhoto(Long calendarId) {
	// 	Calendar calendar = calendarRepository.findById(calendarId)
	// 			.orElseThrow(() -> new IllegalArgumentException(BaseResponseStatus.GET_CALENDAR_EMPTY.getMessage()));
	//
	// 	Photo photo = calendar.getPhoto();
	// 	User user = calendar.getUser();
	//
	// 	if (photo.isShared()) {
	// 		throw new IllegalStateException(BaseResponseStatus.ALREADY_SHARED.getMessage());
	// 	}
	//
	// 	// Update Photo
	// 	Photo updatedPhoto = Photo.builder()
	// 			.id(photo.getId())
	// 			.photoUrl(photo.getPhotoUrl())
	// 			.isShared(true)
	// 			.isDeleted(photo.isDeleted())
	// 			.build();
	// 	photoRepository.save(updatedPhoto);
	//
	// 	// Create Board
	// 	Board board = Board.builder()
	// 			.user(user)
	// 			.photo(updatedPhoto)
	// 			.hit(0)
	// 			.isDeleted(false)
	// 			.build();
	// 	boardRepository.save(board);

}
