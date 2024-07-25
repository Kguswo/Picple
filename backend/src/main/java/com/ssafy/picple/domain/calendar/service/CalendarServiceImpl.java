package com.ssafy.picple.domain.calendar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.board.entity.Board;
import com.ssafy.picple.domain.board.repository.BoardRepository;
import com.ssafy.picple.domain.calendar.entity.Calendar;
import com.ssafy.picple.domain.calendar.repository.CalendarRepository;
import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.photo.repository.PhotoRepository;
import com.ssafy.picple.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarServiceImpl implements CalendarService {

	private final CalendarRepository calendarRepository;
	private final BoardRepository boardRepository;
	private final PhotoRepository photoRepository;

	// 캘린더에서 보드로 공유하기
	public void sharePhoto(Long calendarId) {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new IllegalArgumentException(BaseResponseStatus.GET_CALENDAR_EMPTY.getMessage()));

		Photo photo = calendar.getPhoto();
		User user = calendar.getUser();

		if (photo.isShared()) {
			throw new IllegalStateException(BaseResponseStatus.ALREADY_SHARED.getMessage());
		}

		Photo sharedPhoto = Photo.builder()
				.id(photo.getId())
				.photoUrl(photo.getPhotoUrl())
				.isShared(true)
				.isDeleted(photo.isDeleted())
				.build();
		photoRepository.save(sharedPhoto);

		Board board = Board.builder()
				.user(user)
				.photo(sharedPhoto)
				.hit(0)
				.isDeleted(false)
				.build();
		boardRepository.save(board);
	}

	// 캘린더에서 사진 삭제
	public void deleteCalendar(Long calendarId) {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new IllegalArgumentException(BaseResponseStatus.GET_CALENDAR_EMPTY.getMessage()));

		calendarRepository.delete(calendar);
	}
}
