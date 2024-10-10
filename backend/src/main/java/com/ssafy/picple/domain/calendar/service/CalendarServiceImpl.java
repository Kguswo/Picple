package com.ssafy.picple.domain.calendar.service;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.config.baseresponse.BaseException;
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
	public Long getPhotoCounts(Long userId, LocalDate createdAt) throws BaseException {
		try {
			return calendarRepository.countByUserIdAndDate(userId, createdAt);
		} catch (Exception e) {
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// 캘린더 달력(년월)별 사진 개수 조회
	@Override
	public List<Long> getMonthlyPhotoCounts(Long userId, LocalDate monthlyStartDate, LocalDate monthlyEndDate) throws
			BaseException {
		try {
			// 월별 날짜 리스트 생성
			// datesUntil 메서드로 monthlyStartDate부터 monthlyEndDate-1 까지의 Stream 생성 -> 이후 리스트로
			List<LocalDate> monthlyDates = monthlyStartDate.datesUntil(monthlyEndDate.plusDays(1)).toList();

			// 사진 개수 저장할 리스트
			List<Long> monthlyPhotoCounts = new ArrayList<>();
			for (LocalDate date : monthlyDates) {
				Long count = calendarRepository.countByUserIdAndDate(userId, date);
				monthlyPhotoCounts.add(count);
			}
			return monthlyPhotoCounts;
		} catch (Exception e) {
			throw new BaseException(DATABASE_ERROR);
		}
	}

	// 캘린더 일별 정보 조회
	@Override
	public List<CalendarDto> getDailyCalendars(Long userId, LocalDate createdAt) throws BaseException {
		try {
			List<Calendar> calendars = calendarRepository.findByUserIdAndCreatedAt(userId, createdAt);
			return calendars.stream()
					.map(calendar -> {
						Long photoId = calendar.getPhoto().getId();
						// content를 PhotoUser에서 가져와야 함 Calendar - Photo - PhotoUser로 연결되어 있음
						// 여기서 content 가져와야 함
						PhotoUser photoUser = photoUserRepository.findByPhotoIdAndUserId(photoId, userId);
						String content = photoUser != null ? photoUser.getContent() : "";

						return new CalendarDto(
								calendar.getId(),
								calendar.getPhoto().getPhotoUrl(),
								content
						);
					}).collect(Collectors.toList());
		} catch (Exception e) {
			throw new BaseException(DATABASE_ERROR);
		}
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
		if (Objects.equals(userId, user.getId())) {

			// 특정 사용자의 특정 사진 content찾기
			PhotoUser photoUser = photoUserRepository.findByPhotoIdAndUserId(photo.getId(), userId);

			if (photoUser != null) {
				photoUser.setContent(content);
			} else {
				// photoUser가 null
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

		if (!Objects.equals(userId, user.getId())) {
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

	// 캘린더에서 특정 사진 선택하여 로컬에 다운로드
	@Override
	public void downloadPhoto(Long calendarId, Long userId) throws BaseException {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new BaseException(GET_CALENDAR_EMPTY));

		User user = calendar.getUser();
		if (userId.equals(user.getId())) {
			// 다운로드 받을 사진 경로와 저장될 default 이름 설정(중복고려)
			String photoUrl = calendar.getPhoto().getPhotoUrl();
			String fileName = "picple-download" + "_" + System.currentTimeMillis() + ".jpg";

			/**
			 * 여러 운영체제 호환 고려한 다운로드 경로 -> home디렉토리 가져오고 그 뒤에 폴더 추가
			 * openStream으로 URL에 대한 입력 스트림을 열고 URL이 가리키는 자원 데이터를 읽기
			 * copy메서드로 경로에서 파일 읽어와 특정경로에 복사
			 */
			String downloadDirPath =
					System.getProperty("user.home") + File.separator + "Downloads" + File.separator + fileName;
			Path photoPath = Paths.get(downloadDirPath);

			try (InputStream in = new URL(photoUrl).openStream()) {
				Files.copy(in, photoPath);
			}  catch (FileNotFoundException e) {
				throw new BaseException(FILE_NOT_FOUND_ERROR);

			} catch (IOException e) {
				e.printStackTrace(); // 디버깅을 위한 로그

				throw new BaseException(FILE_DOWNLOAD_ERROR);
			}

		} else {
			throw new BaseException(NOT_EQUAL_USER_ID);
		}

	}

	// 캘린더Id에 해당하는 PhotoUrl 조회(사진 다운로드용)
	@Override
	public String getPhotoUrlByCalendarId(Long calendarId, Long userId) throws BaseException {
		Calendar calendar = calendarRepository.findById(calendarId)
				.orElseThrow(() -> new BaseException(GET_CALENDAR_EMPTY));
		return calendar.getPhoto().getPhotoUrl();
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

		// board에도 존재하면 여기서도 삭제 진행
		if (board != null) {
			boardService.deleteBoard(board.getId(), board.getUser().getId());
		}

	}
}
