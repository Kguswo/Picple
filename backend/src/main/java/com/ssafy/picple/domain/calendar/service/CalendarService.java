package com.ssafy.picple.domain.calendar.service;

public interface CalendarService {

	// 캘린더에서 보드로 공유하기
	void sharePhoto(Long calendarId);

	// 캘린더에서 사진 삭제
	void deleteCalendar(Long calendarId);
}
