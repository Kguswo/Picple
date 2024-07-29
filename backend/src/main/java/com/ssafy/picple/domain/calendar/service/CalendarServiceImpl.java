package com.ssafy.picple.domain.calendar.service;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.config.baseResponse.BaseException;
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
					// 여기서 content가져와야함
					PhotoUser photoUser = photoUserRepository.findByPhotoIdAndUserId(photoId, userId);
					String content = photoUser != null ? photoUser.getContent() : "";

					return new CalendarDto(
							calendar.getId(),
							calendar.getPhoto().getPhotoUrl(),
							content
					);
				}).collect(Collectors.toList());
	}

	// 캘린더 선택 사진별 설명 작성
	// 로그인 유저와 선택캘린더 userId 비교후 하려했지만 이미 캘린더 페이지는 로그인 체크를 마쳤기에 패스
	@Override
	@Transactional
	public void updateContent(Long calendarId, String content) throws BaseException {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new IllegalArgumentException(BaseResponseStatus.GET_CALENDAR_EMPTY.getMessage()));
		Photo photo = calendar.getPhoto();
		User user = calendar.getUser();
		PhotoUser photoUser = photoUserRepository.findByPhotoIdAndUserId(photo.getId(),
				user.getId()); // 특정 사용자의 특정 사진 content찾기

		if (photoUser != null) {
			photoUser.setContent(content);
		} else {
			throw new BaseException(GET_PHOTO_USER_EMPTY);
		}

		photoUserRepository.save(photoUser);
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
