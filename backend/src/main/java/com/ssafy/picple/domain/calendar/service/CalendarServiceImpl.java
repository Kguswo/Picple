package com.ssafy.picple.domain.calendar.service;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.board.entity.Board;
import com.ssafy.picple.domain.board.repository.BoardRepository;
import com.ssafy.picple.domain.board.service.BoardService;
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
@Transactional(readOnly = true)
public class CalendarServiceImpl implements CalendarService {

	private final CalendarRepository calendarRepository;
	private final BoardRepository boardRepository;
	private final PhotoRepository photoRepository;
	private final PhotoUserRepository photoUserRepository;
	private final BoardService boardService;

	// 캘린더 날짜(년월일)별 사진 개수 조회
	@Override
	public Long getPhotoCounts(Long userId, LocalDate createdAt) {
		return calendarRepository.countByUserIdAndDate(userId, createdAt);
	}

	// 캘린더 일별 정보 조회
	@Override
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
	public void updateContent(Long calendarId, Long userId, String content) throws BaseException {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new BaseException(GET_CALENDAR_EMPTY));

		User user = calendar.getUser();
		Photo photo = calendar.getPhoto();

		// 현재 접속자와 캘린더 작성자가 같은지 확인
		if (userId == user.getId()) {

			// 특정 사용자의 특정 사진 content찾기
			PhotoUser photoUser = photoUserRepository.findByPhotoIdAndUserId(photo.getId(), userId);

			if (photoUser != null) {
				photoUser.setContent(content);
			} else {
				throw new BaseException(GET_PHOTO_USER_EMPTY);
			}

			photoUserRepository.save(photoUser);

		} else {
			throw new BaseException(NOT_EQUAL_USER_ID);
		}

	}

	// 캘린더에서 보드로 공유하기 - isShared = true로
	@Override
	@Transactional
	public void sharePhoto(Long calendarId, Long userId) throws BaseException {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new BaseException(GET_CALENDAR_EMPTY));

		Photo photo = calendar.getPhoto();
		User user = calendar.getUser();

		if (userId != user.getId()) {
			throw new BaseException(NOT_EQUAL_USER_ID);
		}

		Photo selectedPhoto = photoRepository.findByPhotoUrl(photo.getPhotoUrl());
		if (selectedPhoto != null) {
			// 선택된 사진이 이미 공유된 경우 예외처리
			if (selectedPhoto.isShared()) {
				throw new BaseException(ALREADY_SHARED);
			} else {
				selectedPhoto.setIsShared(true);
				photoRepository.save(selectedPhoto);

				// 보드에 추가할 Board생성
				Board board = Board.builder()
						.user(user)
						.photo(selectedPhoto)
						.hit(0)
						.isDeleted(false)
						.build();
				boardRepository.save(board);
			}
		}
	}

	// 캘린더에서 사진 삭제
	// 삭제시 board에서도 삭제됨.
	@Override
	@Transactional
	public void deleteCalendar(Long calendarId, Long userId) throws BaseException {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new BaseException(GET_CALENDAR_EMPTY));

		calendarRepository.delete(calendar);

		Board board = boardRepository.findByUserIdAndPhotoIdAndIsDeletedFalse(calendar.getUser().getId(),
				calendar.getPhoto().getId());

		boardService.deleteBoard(board.getId(), board.getUser().getId());
	}
}
