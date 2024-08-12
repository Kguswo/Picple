package com.ssafy.picple.domain.photo.service;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.aws.S3FileUploadService;
import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.domain.calendar.entity.Calendar;
import com.ssafy.picple.domain.calendar.repository.CalendarRepository;
import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.photo.repository.PhotoRepository;
import com.ssafy.picple.domain.photouser.entity.PhotoUser;
import com.ssafy.picple.domain.photouser.repository.PhotoUserRepository;
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
	private final PhotoUserRepository photoUserRepository;
	private final S3FileUploadService s3FileUploadService;

	// 사진 저장 -> Photo와 Calendar에 모두 저장, photoUser에 정보 추가
	@Override
	public Photo insertPhoto(Long userId, MultipartFile file) throws BaseException, IOException {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BaseException(GET_USER_EMPTY));

		// 사진을 S3에 업로드 후 photoUrl 가져오기
		String photoUrl = s3FileUploadService.uploadFile(file);

		// 기존에 같은 photoUrl있는지 확인(같은사진 여러번 못 들어가도록)
		Photo existingPhoto = photoRepository.findByPhotoUrl(photoUrl);
		if (existingPhoto == null) {
			// Photo 생성 및 저장
			Photo newPhoto = Photo.builder()
					.photoUrl(photoUrl)
					.isShared(false)
					.isDeleted(false)
					.build();
			existingPhoto = photoRepository.save(newPhoto);
		}

		// 사진을 Calendar에 저장, Calendar에서 중복된 기록이 있는지 확인해야함
		boolean calendarExists = calendarRepository.existsByPhotoIdAndUserId(existingPhoto.getId(), userId);
		if (!calendarExists) {
			Calendar newCalendar = Calendar.builder()
					.photo(existingPhoto)
					.user(user)
					.build();
			calendarRepository.save(newCalendar);
		}

		// PhotoUser에 userId와 photoId 정보 저장, photoUser에서 중복된 기록이 있는지 확인해야함
		boolean photoUserExists = photoUserRepository.existsByPhotoIdAndUserId(existingPhoto.getId(), userId);
		if (!photoUserExists) {
			PhotoUser photoUser = PhotoUser.builder()
					.photo(existingPhoto)
					.user(user)
					.content("") // 생성 시 빈 값
					.build();
			photoUserRepository.save(photoUser);
		}

		return existingPhoto;
	}

}
