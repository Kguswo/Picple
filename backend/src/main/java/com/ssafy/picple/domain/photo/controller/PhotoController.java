package com.ssafy.picple.domain.photo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.AwsS3.S3FileUploadService;
import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.photo.service.PhotoService;
import com.ssafy.picple.domain.photouser.entity.PhotoUser;
import com.ssafy.picple.domain.photouser.repository.PhotoUserRepository;
import com.ssafy.picple.domain.user.entity.User;
import com.ssafy.picple.domain.user.repository.UserRepository;
import com.ssafy.picple.util.JWTUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
public class PhotoController {

	private final PhotoService photoService;
	private final S3FileUploadService s3FileUploadService;
	private final JWTUtil jwtUtil;
	private final PhotoUserRepository photoUserRepository;
	private final UserRepository userRepository;

	// 사진 저장
	// 사람마다 각자 저장하는데 사진URL자체는 같기 때문에 이거 고민하고 수정해야함 -> 시간만 갱신되고 추가되지 않는것 확인
	@PostMapping(value = "", consumes = "multipart/form-data")
	//	public BaseResponse<?> savePhoto(@RequestPart("photo") @Valid Photo photo, @RequestPart("file") MultipartFile file, @RequestHeader String token) {
	public BaseResponse<?> savePhoto(@RequestPart("photo") @Valid Photo photo,
			@RequestPart("file") MultipartFile file) {
		try {
			String photoUrl = s3FileUploadService.uploadFile(file);

			Photo newPhoto = Photo.builder()
					.photoUrl(photoUrl)
					.isShared(false)
					.isDeleted(false)
					.build();
			Photo savedPhoto = photoService.insertPhoto(newPhoto);

			// TODO
			// 일단 USERID 1로 임시 테스트 수정해야함
			User user = userRepository.findById(1L).get();

			// PhotoUser 생성 및 저장
			PhotoUser photoUser = PhotoUser.builder()
					.photo(savedPhoto)
					.user(user)
					.content("") // 생성시 빈 값
					.build();
			photoUserRepository.save(photoUser);

			return new BaseResponse<>(savedPhoto);
		} catch (Exception e) {
			return new BaseResponse<>(BaseResponseStatus.FILE_UPLOAD_ERROR);
		}
	}

}
