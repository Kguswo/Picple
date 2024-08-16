package com.ssafy.picple.domain.photo.service;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.domain.photo.entity.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {

	// 사진 저장
	Photo insertPhoto(Long userId, MultipartFile file) throws BaseException, IOException;

}
