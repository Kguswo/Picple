package com.ssafy.picple.domain.photo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseResponse.BaseResponse;
import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.photo.service.PhotoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
public class PhotoController {

	private final PhotoService photoService;

	@PostMapping("")
	public BaseResponse<?> savePhoto(@RequestBody Photo photo) {
		return new BaseResponse<>(photoService.insertPhoto(photo));
	}

}
