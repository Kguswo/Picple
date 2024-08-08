package com.ssafy.picple.domain.background.service;

import static com.ssafy.picple.config.baseResponse.BaseResponseStatus.*;

import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.picple.config.baseResponse.BaseException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LocalFileService {

	private final FileUploadService fileUploadService;

	/**
	 * 사용자의 로컬에서 사진을 불러와 파일을 Base64로 인코딩하고
	 * Base64와 PNG로 된 사진 이름을 반환하는 메서드
	 *
	 * @return 업로드된 파일의 S3 URL을 포함하는 문자열을 반환
	 * @throws BaseException 예외 발생 시
	 */
	public String[] createLocalImageBackground(MultipartFile file) throws BaseException {
		try {
			String base64Image = convertToBase64Mono(file).block();
			String fileName = fileUploadService.generateFileName("local", ".png"); // 포맷을 PNG로 가정

			return new String[] {base64Image, fileName};
		} catch (Exception e) {
			// 기타 모든 예외 처리
			throw new BaseException(BACKGROUND_UPLOAD_ERROR);
		}
	}

	/**
	 * MultipartFile을 Base64로 인코딩한 후 Mono<String>으로 변환하는 메서드
	 *
	 * @param file 업로드할 파일
	 * @return Base64 인코딩된 문자열을 포함하는 Mono 객체
	 * @throws BaseException 예외 발생 시
	 */
	public Mono<String> convertToBase64Mono(MultipartFile file) throws BaseException {
		try {
			byte[] fileBytes = file.getBytes();
			String base64Image = Base64.getEncoder().encodeToString(fileBytes);
			return Mono.just(base64Image);
		} catch (IOException e) {
			throw new BaseException(FILE_CONVERSION_ERROR); // 파일 변환 오류 처리
		}
	}
}
