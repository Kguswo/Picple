package com.ssafy.picple.domain.calendar.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.board.entity.Board;
import com.ssafy.picple.domain.board.repository.BoardRepository;
import com.ssafy.picple.domain.calendar.dto.CalendarDto;
import com.ssafy.picple.domain.calendar.entity.Calendar;
import com.ssafy.picple.domain.calendar.repository.CalendarRepository;
import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.photo.repository.PhotoRepository;
import com.ssafy.picple.domain.photouser.entity.PhotoUser;
import com.ssafy.picple.domain.photouser.repository.PhotoUserRepository;
import com.ssafy.picple.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

	private final CalendarRepository calendarRepository;
	private final BoardRepository boardRepository;
	private final PhotoRepository photoRepository;
	private final PhotoUserRepository photoUserRepository;

	// 캘린더 전체 조회
	@Override
	@Transactional(readOnly = true)
	public List<Calendar> getAllCalendars() {
		return calendarRepository.findAll();
	}

	// 캘린더 날짜(년월일)별 사진 개수 조회
	@Override
	@Transactional(readOnly = true)
	public Long getPhotoCounts(Long userId, LocalDate createdAt) {
		return calendarRepository.countByUserIdAndDate(userId, createdAt);
	}

	// 캘린더 일별 정보 조회
	@Override
	@Transactional(readOnly = true)
	public List<CalendarDto> getDailyCalendars(Long userId, LocalDate createdAt) {
		List<Calendar> calendars = calendarRepository.findByUserIdAndCreatedAt(userId, createdAt);
		return calendars.stream()
				.map(calendar -> {
					Long photoId = calendar.getPhoto().getId();
					// content를 PhotoUser에서 가져와야함 Calendar - Photo - PhotoUser로 연결되어있음
					PhotoUser photoUser = photoUserRepository.findByPhotoIdAndUserId(photoId,
							userId); // 여기서 content가져와야함
					String content = photoUser != null ? photoUser.getContent() : "";

					return new CalendarDto(
							calendar.getId(),
							calendar.getPhoto().getPhotoUrl(),
							content
					);
				}).collect(Collectors.toList());
	}

	// 캘린더 선택 사진별 설명 작성
	@Override
	@Transactional
	public void addContent(Long calendarId, String description) {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new IllegalArgumentException(BaseResponseStatus.GET_CALENDAR_EMPTY.getMessage()));
		calendar.setDescription(description);
		calendarRepository.save(calendar);
	}

	// 캘린더 선택 사진별 설명 수정
	@Override
	@Transactional
	public void modifyContent(Long calendarId, String description) {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new IllegalArgumentException(BaseResponseStatus.GET_CALENDAR_EMPTY.getMessage()));
		calendar.setDescription(description);
		calendarRepository.save(calendar);
	}

	// 캘린더 선택 사진별 설명 삭제
	@Override
	@Transactional
	public void deleteContent(Long calendarId) {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new IllegalArgumentException(BaseResponseStatus.GET_CALENDAR_EMPTY.getMessage()));
		calendar.setDescription(null);
		calendarRepository.save(calendar);
	}

	// 캘린더에서 보드로 공유하기
	@Override
	@Transactional
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
	@Override
	@Transactional
	public void deleteCalendar(Long calendarId) {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new IllegalArgumentException(BaseResponseStatus.GET_CALENDAR_EMPTY.getMessage()));

		calendarRepository.delete(calendar);
	}
}
