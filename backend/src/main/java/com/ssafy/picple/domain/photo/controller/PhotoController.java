package com.ssafy.picple.domain.photo.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponse;
import com.ssafy.picple.domain.photo.entity.Photo;
import com.ssafy.picple.domain.photo.service.PhotoService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
public class PhotoController {

	private final PhotoService photoService;

	/**
	 * 사진 저장
	 * 사람마다 각자 저장하는데 사진URL자체는 같기 때문에 수정 -> 시간만 갱신되고 추가되지 않는것 확인
	 *
	 * @param request
	 * @param file
	 * @return
	 * @throws BaseException
	 * @throws IOException
	 */
	@PostMapping(value = "", consumes = "multipart/form-data")
	public BaseResponse<Photo> savePhoto(HttpServletRequest request,
			@RequestPart("file") MultipartFile file) throws BaseException, IOException {

		Long userId = (Long)request.getAttribute("userId");
		System.out.println("userId = " + userId);
		Photo savedPhoto = photoService.insertPhoto(userId, file);

		return new BaseResponse<>(savedPhoto);

	}

}
