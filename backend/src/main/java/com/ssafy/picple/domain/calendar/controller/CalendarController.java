package com.ssafy.picple.domain.calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.calendar.service.CalendarService;
import com.ssafy.picple.domain.calendar.dto.CalendarDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

	private final CalendarService calendarService;

	// 캘린더 날짜(년월일)별 사진 개수 조회
	@GetMapping("/counts")
	@Transactional(readOnly = true)
	public BaseResponse<Long> getPhotoCounts(@RequestParam("userId") Long userId, @RequestParam("createdAt") String createdAt) {
		try {
			// 문자열 파싱 후, LocalDateTime -> LocalDate
			LocalDateTime dateTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			LocalDate date = dateTime.toLocalDate();

			Long count = calendarService.getPhotoCounts(userId, date);
			return new BaseResponse<>(count);
		} catch (Exception e) {
			return new BaseResponse<>(BaseResponseStatus.DATABASE_ERROR);
		}
	}

	// 캘린더 일별 정보 조회
	@GetMapping("/daily")
	public BaseResponse<List<CalendarDto>> getDailyCalendars(@RequestParam Long userId, @RequestParam("createdAt") String createdAt) {
		try {
			// 문자열 파싱
			LocalDate date = LocalDate.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			List<CalendarDto> calendars = calendarService.getDailyCalendars(userId, date);
			return new BaseResponse<>(calendars);
		} catch (Exception e) {
			return new BaseResponse<>(BaseResponseStatus.DATABASE_ERROR);
		}
	}

	// 캘린더 선택 사진별 설명 작성
	@PostMapping("/{calendarId}")
	public BaseResponse<BaseResponseStatus> updateContent(@PathVariable Long calendarId, @RequestParam String content) {
		try {
			calendarService.updateContent(calendarId, content);
			return new BaseResponse<>(BaseResponseStatus.SUCCESS);
		} catch (Exception e) {
			return new BaseResponse<>(BaseResponseStatus.DATABASE_ERROR);
		}
	}

	// 캘린더에서 보드로 공유
	@PostMapping("/share/{calendarId}")
	@Transactional
	public BaseResponse<BaseResponseStatus> shareCalendar(@PathVariable("calendarId") Long calendarId) {

		try {
			calendarService.sharePhoto(calendarId);
			return new BaseResponse<>(BaseResponseStatus.SUCCESS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	// 캘린더에서 사진 삭제
	@DeleteMapping("/{calendarId}")
	@Transactional
	public BaseResponse<?> deleteCalendar(@PathVariable("calendarId") Long calendarId) {
		try {
			calendarService.deleteCalendar(calendarId);
			return new BaseResponse<>(BaseResponseStatus.SUCCESS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
