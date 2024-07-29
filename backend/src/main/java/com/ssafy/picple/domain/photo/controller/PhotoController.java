package com.ssafy.picple.domain.photo.controller;

import com.ssafy.picple.config.baseResponse.BaseException;
import com.ssafy.picple.domain.board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
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

import java.io.IOException;

@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
public class PhotoController {

	private final PhotoService photoService;
	private final BoardService boardService;

	/**
	 * 사진 저장
	 * 사람마다 각자 저장하는데 사진URL자체는 같기 때문에 이거 고민하고 수정해야함 -> 시간만 갱신되고 추가되지 않는것 확인
	 * @param request
	 * @param file
	 * @return
	 * @throws BaseException
	 * @throws IOException
	 */
	@PostMapping(value = "", consumes = "multipart/form-data")
	public BaseResponse<Photo> savePhoto(HttpServletRequest request,
									 @RequestPart("file") MultipartFile file) throws BaseException, IOException {

//		Long userId = boardService.getUserId(request);
		Long userId = (Long) request.getAttribute("userId");
		System.out.println("userId = " + userId);
		Photo savedPhoto = photoService.insertPhoto(userId, file);

		return new BaseResponse<>(savedPhoto);

	}

}
