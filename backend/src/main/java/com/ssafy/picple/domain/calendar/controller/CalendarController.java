package com.ssafy.picple.domain.calendar.controller;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponse;
import com.ssafy.picple.config.baseresponse.BaseResponseStatus;
import com.ssafy.picple.domain.calendar.dto.CalendarDto;
import com.ssafy.picple.domain.calendar.service.CalendarService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendars")
@Transactional(readOnly = true)
public class CalendarController {

	private final CalendarService calendarService;

	/**
	 * 캘린더 날짜(년월일)별 사진 개수 조회
	 *
	 * @param request
	 * @param createdAt
	 * @return
	 */
	@GetMapping("/counts")
	public BaseResponse<Long> getPhotoCounts(HttpServletRequest request,
			@RequestParam("createdAt") String createdAt) throws BaseException {

		Long userId = (Long)request.getAttribute("userId");

		// 문자열 파싱 후 -> LocalDate
		LocalDate date = LocalDate.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		Long count = calendarService.getPhotoCounts(userId, date);

		return new BaseResponse<>(count);
	}

	/**
	 * 캘린더 월별 사진 개수 조회
	 *
	 * @param request
	 * @param monthlyStartDate
	 * @param monthlyEndDate
	 * @return
	 */
	@GetMapping("/monthly-counts")
	public BaseResponse<List<Long>> getMonthlyPhotoCounts(HttpServletRequest request,
			@RequestParam("monthlyStartDate") String monthlyStartDate,
			@RequestParam("monthlyEndDate") String monthlyEndDate) throws BaseException {
		Long userId = (Long)request.getAttribute("userId");
		// 문자열 파싱 후 -> LocalDate
		LocalDate startDate = LocalDate.parse(monthlyStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalDate endDate = LocalDate.parse(monthlyEndDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		// 월별 사진 개수 조회
		List<Long> counts = calendarService.getMonthlyPhotoCounts(userId, startDate, endDate);
		return new BaseResponse<>(counts);

	}

	/**
	 * 캘린더 일별 정보 조회
	 *
	 * @param request
	 * @param createdAt
	 * @return
	 */
	@GetMapping("/daily")
	public BaseResponse<List<CalendarDto>> getDailyCalendars(HttpServletRequest request,
			@RequestParam("createdAt") String createdAt) throws BaseException {

		Long userId = (Long)request.getAttribute("userId");
		// 문자열 파싱 -> LocalDate
		LocalDate date = LocalDate.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		List<CalendarDto> calendars = calendarService.getDailyCalendars(userId, date);

		return new BaseResponse<>(calendars);

	}

	/**
	 * 캘린더 선택 사진별 설명 작성
	 *
	 * @param calendarId
	 * @param content
	 * @return
	 * @throws BaseException
	 */
	@PostMapping("/{calendarId}")
	@Transactional
	public BaseResponse<BaseResponseStatus> updateContent(HttpServletRequest request, @PathVariable Long calendarId,
			@RequestParam String content) throws BaseException {

		Long userId = (Long)request.getAttribute("userId");
		calendarService.updateContent(calendarId, userId, content);

		return new BaseResponse<>(SUCCESS);

	}

	/**
	 * 캘린더에서 보드로 공유
	 *
	 * @param calendarId
	 * @return
	 * @throws BaseException
	 */
	@PostMapping("/share/{calendarId}")
	@Transactional
	public BaseResponse<BaseResponseStatus> shareCalendar(HttpServletRequest request,
			@PathVariable("calendarId") Long calendarId) throws BaseException {

		Long userId = (Long)request.getAttribute("userId");
		calendarService.sharePhoto(calendarId, userId);

		return new BaseResponse<>(SUCCESS);

	}

	/**
	 * 캘린더에서 특정 사진 선택하여 로컬에 다운로드
	 *
	 * @param request
	 * @param calendarId
	 * @return
	 * @throws BaseException
	 */
	@PostMapping("/download/{calendarId}")
	@Transactional
	public BaseResponse<BaseResponseStatus> downloadCalendar(HttpServletRequest request,
			@PathVariable Long calendarId) throws BaseException, FileNotFoundException {

		Long userId = (Long)request.getAttribute("userId");
		calendarService.downloadPhoto(calendarId, userId);

		return new BaseResponse<>(SUCCESS);
	}

	/**
	 * 캘린더Id에 해당하는 PhotoUrl 조회(사진 다운로드용)
	 *
	 * @param request
	 * @param calendarId
	 * @return
	 * @throws BaseException
	 */
	@GetMapping("/photo")
	public BaseResponse<String> getPhotos(HttpServletRequest request,
			@RequestParam("calendarId") Long calendarId) throws BaseException {

		Long userId = (Long)request.getAttribute("userId");
		String photoUrl = calendarService.getPhotoUrlByCalendarId(calendarId, userId);

		return new BaseResponse<>(photoUrl);
	}

	/**
	 * 캘린더에서 사진 삭제
	 *
	 * @param calendarId
	 * @return
	 * @throws BaseException
	 */
	@DeleteMapping("/{calendarId}")
	@Transactional
	public BaseResponse<?> deleteCalendar(HttpServletRequest request,
			@PathVariable("calendarId") Long calendarId) throws BaseException {

		Long userId = (Long)request.getAttribute("userId");
		calendarService.deleteCalendar(calendarId, userId);

		return new BaseResponse<>(SUCCESS);

	}

}
