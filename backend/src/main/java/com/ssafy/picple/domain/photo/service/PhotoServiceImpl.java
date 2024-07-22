package com.ssafy.picple.domain.photo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.calendar.entity.Calendar;
import com.ssafy.picple.domain.calendar.repository.CalendarRepository;
import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.photo.repository.PhotoRepository;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotoServiceImpl implements PhotoService {

	private final PhotoRepository photoRepository;
	private final CalendarRepository calendarRepository;
	private final UserRepository userRepository;

	@Override
	public Photo insertPhoto(Photo photo) {
		Long userId = getUserId();
		User user = userRepository.findById(userId) // 임시 유저아이디
				.orElseThrow(() -> new IllegalArgumentException(BaseResponseStatus.GET_USER_EMPTY.getMessage()));
		Photo newPhoto = Photo.builder()
				.photoUrl(photo.getPhotoUrl())
				.isShared(false)
				.isDeleted(false)
				.build();
		photoRepository.save(newPhoto);

		Calendar newCalendar = Calendar.builder()
				.photo(newPhoto)
				.user(user) // 임시 유저아이디
				.build();
		calendarRepository.save(newCalendar);
		return newPhoto;
	}

	// 현재 사용자의 ID, 임시로 반환중 수정해야함
	private Long getUserId() {
		return 1L;
	}

}
