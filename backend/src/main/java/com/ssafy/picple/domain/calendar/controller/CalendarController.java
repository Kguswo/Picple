package com.ssafy.picple.domain.calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ssafy.picple.config.baseResponse.BaseException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.calendar.dto.CalendarDto;
import com.ssafy.picple.domain.calendar.service.CalendarService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendars")
public class CalendarController {

	private final CalendarService calendarService;

	// 캘린더 날짜(년월일)별 사진 개수 조회
	@GetMapping("/counts")
	@Transactional(readOnly = true)
	public BaseResponse<Long> getPhotoCounts(HttpServletRequest request,
											 @RequestParam("createdAt") String createdAt) {
		try {
			Long userId = (Long)request.getAttribute("userId");
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
	public BaseResponse<List<CalendarDto>> getDailyCalendars(@RequestParam Long userId,
															 @RequestParam("createdAt") String createdAt) {

		// 문자열 파싱
		LocalDate date = LocalDate.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		List<CalendarDto> calendars = calendarService.getDailyCalendars(userId, date);
		return new BaseResponse<>(calendars);

	}

	// 캘린더 선택 사진별 설명 작성
	@PostMapping("/{calendarId}")
	public BaseResponse<BaseResponseStatus> updateContent(@PathVariable Long calendarId,
														  @RequestParam String content) throws BaseException {

		calendarService.updateContent(calendarId, content);
		return new BaseResponse<>(BaseResponseStatus.SUCCESS);

	}

	// 캘린더에서 보드로 공유
	@PostMapping("/share/{calendarId}")
	@Transactional
	public BaseResponse<BaseResponseStatus> shareCalendar(@PathVariable("calendarId") Long calendarId) throws BaseException {

		calendarService.sharePhoto(calendarId);
		return new BaseResponse<>(BaseResponseStatus.SUCCESS);

	}

	// 캘린더에서 사진 삭제
	@DeleteMapping("/{calendarId}")
	@Transactional
	public BaseResponse<?> deleteCalendar(@PathVariable("calendarId") Long calendarId) throws BaseException {

		calendarService.deleteCalendar(calendarId);
		return new BaseResponse<>(BaseResponseStatus.SUCCESS);

	}

}
