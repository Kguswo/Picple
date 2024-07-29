package com.ssafy.picple.domain.photo.service;

import com.ssafy.picple.AwsS3.S3FileUploadService;
import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.board.service.BoardService;
import com.ssafy.picple.domain.photouser.entity.PhotoUser;
import com.ssafy.picple.domain.photouser.repository.PhotoUserRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotoServiceImpl implements PhotoService {

	private final PhotoRepository photoRepository;
	private final CalendarRepository calendarRepository;
	private final UserRepository userRepository;
	private final PhotoUserRepository photoUserRepository;
	private final S3FileUploadService s3FileUploadService;

	// 사진 저장 -> Photo와 Calendar에 모두 저장, photoUser에 정보 추가
	@Override
	public Photo insertPhoto(Long userId, MultipartFile file) throws BaseException, IOException {
		// 사진을 S3에 업로드 후 photoUrl 가져오기
		String photoUrl = s3FileUploadService.uploadFile(file);

		// Photo 생성 및 저장
		Photo newPhoto = Photo.builder()
				.photoUrl(photoUrl)
				.isShared(false)
				.isDeleted(false)
				.build();
		newPhoto = photoRepository.save(newPhoto);

		User user = userRepository.findById(userId).get();

		// 사진을 Calendar에 저장
		Calendar newCalendar = Calendar.builder()
				.photo(newPhoto)
				.user(user)
				.build();
		calendarRepository.save(newCalendar);

		// PhotoUser에 userId와 photoId 정보 저장
		PhotoUser photoUser = PhotoUser.builder()
				.photo(newPhoto)
				.user(user)
				.content("") // 생성시 빈 값
				.build();
		photoUserRepository.save(photoUser);

		return newPhoto;
	}

}
