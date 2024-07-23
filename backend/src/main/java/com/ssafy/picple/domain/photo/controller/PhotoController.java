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
@CrossOrigin("*")
public class PhotoController {

	private final PhotoService photoService;
	private final S3FileUploadService s3FileUploadService;

	@PostMapping(value = "", consumes = "multipart/form-data")
	public BaseResponse<?> savePhoto(@RequestPart("photo") @Valid Photo photo,
			@RequestPart("file") MultipartFile file) {
		try {
			System.out.println("사진저장 전");
			String photoUrl = s3FileUploadService.uploadFile(file);
			System.out.println("사진저장 후");

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
