package com.ssafy.picple.domain.photo.controller;

import com.ssafy.picple.AwsS3.S3FileUploadService;
import com.ssafy.picple.config.baseResponse.BaseResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.photo.service.PhotoService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
public class PhotoController {

	private final PhotoService photoService;
	private final S3FileUploadService s3FileUploadService;

	@PostMapping("")
	public BaseResponse<?> savePhoto(@RequestBody Photo photo, MultipartFile file) {
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
