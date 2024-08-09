package com.ssafy.picple.aws;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponseStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3FileUploadService {
	private static final Logger logger = Logger.getLogger(S3FileUploadService.class.getName());
	private final AmazonS3 s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	private final String defaultUrl = "https://picple.s3.ap-northeast-2.amazonaws.com";

	public String uploadFile(MultipartFile file) throws IOException, BaseException {
		// String originalFilename = file.getOriginalFilename();
		String originalFilename = generateFileName(file);

		try {
			s3Client.putObject(bucketName, originalFilename, file.getInputStream(), getObjectMetadata(file));
			return defaultUrl + "/" + originalFilename;
		} catch (SdkClientException e) {
			throw new BaseException(BaseResponseStatus.FILE_UPLOAD_ERROR);
		}
	}

	// MultipartFile 사이즈랑 타입 명시용
	private ObjectMetadata getObjectMetadata(MultipartFile file) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(file.getContentType());
		objectMetadata.setContentLength(file.getSize());
		return objectMetadata;
	}

	private String generateFileName(MultipartFile file) {
		// 중복안되게 랜덤하게 넣으려면 이렇게 그때그때 UUID붙여서
		return LocalDate.now().toString() + "-" + UUID.randomUUID().toString() + ".jpg";
		// return file.getOriginalFilename();
	}

	// 인코딩 필요하면 사용
	// 파일 이름을 UTF-8로 인코딩
	public static String encodeFileName(String fileName) {
		try {
			return URLEncoder.encode(fileName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return fileName; // 인코딩 실패 시 원래 파일 이름 리턴
		}
	}

	public S3Object downloadFile(String fileName) throws BaseException {
		try {
			return s3Client.getObject(new GetObjectRequest(bucketName, fileName));
		} catch (SdkClientException e) {
			throw new BaseException(BaseResponseStatus.FILE_DOWNLOAD_ERROR);
		}
	}

	public void deleteFile(String file) throws BaseException {
		try {
			s3Client.deleteObject(new DeleteObjectRequest(bucketName, file));
		} catch (SdkClientException e) {
			throw new BaseException(BaseResponseStatus.FILE_DELETE_ERROR);
		}
	}

}
