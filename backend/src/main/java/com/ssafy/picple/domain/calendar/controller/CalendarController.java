package com.ssafy.picple.domain.calendar.controller;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.calendar.service.CalendarService;

import lombok.RequiredArgsConstructor;

@RestController
@Transactional
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

	private final CalendarService calendarService;

	// 캘린더에서 보드로 공유
	@PostMapping("/{calendarId}")
	public BaseResponse<?> shareCalendar(@PathVariable("calendarId") Long calendarId) {
		try {
			calendarService.sharePhoto(calendarId);
			return new BaseResponse<>(BaseResponseStatus.SUCCESS);
		} catch (IllegalStateException e) {
			return new BaseResponse<>(BaseResponseStatus.ALREADY_SHARED);
		} catch (IllegalArgumentException e) {
			return new BaseResponse<>(BaseResponseStatus.GET_CALENDAR_EMPTY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 캘린더에서 사진 삭제
	@DeleteMapping("/{calendarId}")
	public BaseResponse<?> deleteCalendar(@PathVariable("calendarId") Long calendarId) {
		try {
			calendarService.deleteCalendar(calendarId);
			return new BaseResponse<>(BaseResponseStatus.SUCCESS);
		} catch (IllegalArgumentException e) {
			return new BaseResponse<>(BaseResponseStatus.GET_CALENDAR_EMPTY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
