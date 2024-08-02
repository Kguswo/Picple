package com.ssafy.picple.domain.calendar.service;

import java.time.LocalDate;
import java.util.List;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.calendar.dto.CalendarDto;

public interface CalendarService {

	// 캘린더 날짜(년월일)별 사진 개수 조회
	Long getPhotoCounts(Long userId, LocalDate createdAt);

	// 캘린더 일별 정보 조회
	List<CalendarDto> getDailyCalendars(Long userId, LocalDate createdAt);

	// 캘린더 선택 사진별 설명 작성
	void updateContent(Long calendarId, Long userId, String content) throws BaseException;

	// 캘린더에서 보드로 공유하기
	void sharePhoto(Long calendarId, Long userId) throws BaseException;

	// 캘린더에서 사진 삭제
	void deleteCalendar(Long calendarId, Long userId) throws BaseException;
}
