package com.ssafy.picple.domain.photo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
@CrossOrigin("*") // 뺴고 테스트
public class PhotoController {

	private final PhotoService photoService;
	private final S3FileUploadService s3FileUploadService;

	// 사진 저장
	// 사람마다 각자 저장하는데 사진URL자체는 같기 때문에 이거 고민하고 수정해야함
	@PostMapping(value = "", consumes = "multipart/form-data")
	public BaseResponse<?> savePhoto(@RequestPart("photo") @Valid Photo photo,
			@RequestPart("file") MultipartFile file) {
		try {
			String photoUrl = s3FileUploadService.uploadFile(file);

			Photo newPhoto = Photo.builder()
					.photoUrl(photoUrl)
					.isShared(false)
					.isDeleted(false)
					.build();
			return new BaseResponse<>(photoService.insertPhoto(newPhoto));
		} catch (Exception e) {
			return new BaseResponse<>(BaseResponseStatus.FILE_UPLOAD_ERROR);
		}
	}

}
