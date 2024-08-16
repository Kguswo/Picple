package com.ssafy.picple.domain.user.service;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.util.RedisUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender javaMailSender;
	private final RedisUtil redisUtil;

	@Value("${spring.mail.username}")
	private static String senderEmail;

	// TODO: 수정 필요
	// 인증코드 이메일 발송
	public String sendEmail(String toEmail) throws BaseException {
		// 레디스에 같은 키를 가진 값이 있는지 확인
		// 있으면 삭제
		if (redisUtil.existData(toEmail)) {
			redisUtil.deleteData(toEmail);
		}
		// 이메일 폼 생성
		MimeMessage emailForm = createEmailForm(toEmail);
		// 이메일 발송
		javaMailSender.send(emailForm);
		return null;
	}

	// 이메일 폼 생성
	private MimeMessage createEmailForm(String email) throws BaseException {
		String authCode = createCode();
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			message.addRecipients(MimeMessage.RecipientType.TO, email);
			message.setSubject("안녕하세요. 인증번호입니다.");
			message.setFrom(senderEmail);
			message.setText(setContext(authCode), "utf-8", "html");

			// Redis 에 해당 인증코드 인증 시간 설정
			redisUtil.setDataExpire(email, authCode, 60 * 30L);

			return message;
		} catch (MessagingException e) {
			throw new BaseException(EMAIL_SEND_ERROR);
		}
	}

	// 코드 생성
	private String createCode() {
		int leftLimit = 48; // number '0'
		int rightLimit = 122; // alphabet 'z'
		int targetStringLength = 6;
		Random random = new Random();

		return random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 | i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	// 이메일 내용 초기화
	private String setContext(String code) {
		Context context = new Context();
		TemplateEngine templateEngine = new TemplateEngine();
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

		context.setVariable("code", code);

		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(false);

		templateEngine.setTemplateResolver(templateResolver);

		return templateEngine.process("mail", context);
	}

	// 코드 검증
	public String verifyEmailCode(String email, String code) throws BaseException {
		String codeFoundByEmail = redisUtil.getData(email);
		if (codeFoundByEmail == null) {
			throw new BaseException(NOT_EXISTS_CODE);
		}
		if (codeFoundByEmail.equals(code)) {
			return "SUCCESS";
		} else {
			throw new BaseException(NOT_EQUAL_EMAIL_CODE);
		}
	}

}
