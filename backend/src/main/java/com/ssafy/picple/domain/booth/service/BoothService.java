package com.ssafy.picple.domain.booth.service;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ssafy.picple.config.baseresponse.BaseException;

import lombok.RequiredArgsConstructor;

/**
 * 부스의 생성, 참여, 나가기와 같은 비즈니스 로직 처리
 * Redis를 통해 부스의 상태 관리
 */

@Service
@RequiredArgsConstructor
public class BoothService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BOOTH_PREFIX = "booth:"; // Redis에서 부스 식별을 위한 키 접두사
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // 부스 ID 생성을 위함
    private static final int ID_LENGTH = 10;

    /**
     * 고유한 부스 ID 생성
     *
     * @return 생성된 고유한 부스 ID
     */
    private String generateUniqueBoothId() {
        StringBuilder builder = new StringBuilder();
        SecureRandom random = new SecureRandom();

        // 생성
        while (builder.length() < ID_LENGTH) {
            int index = random.nextInt(ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(index));
        }

        String boothId = builder.toString();

        // 중복 검사
        while (Boolean.TRUE.equals(redisTemplate.hasKey(BOOTH_PREFIX + boothId))) {
            boothId = generateUniqueBoothId();
        }

        return boothId;
    }

    /**
     * 부스 생성
     *
     * @param name 부스 이름
     * @param maxParticipants 최대 참가자 수
     * @return 생성된 부스의 ID
     * @throws BaseException 부스 생성 중 오류가 발생할 경우 예외 발생
     */
    public String createBooth(String name, int maxParticipants) throws BaseException {
        try {
            String boothId = generateUniqueBoothId(); // 부스의 고유 ID 생성
            String boothKey = BOOTH_PREFIX + boothId; // Redis 키 생성

            // Redis에 부스 정보 저장
            redisTemplate.opsForHash().put(boothKey, "name", name);
            redisTemplate.opsForHash().put(boothKey, "maxParticipants", String.valueOf(maxParticipants));
            redisTemplate.opsForHash().put(boothKey, "currentParticipants", "0");

            // 부스 정보를 2시간 동안 유지
            redisTemplate.expire(boothKey, 2, TimeUnit.HOURS);

            return boothId;
        } catch (Exception e) {
            throw new BaseException(BOOTH_GENERATION_ERROR);
        }

    }

    /**
     * 부스 참가
     *
     * @param boothId 참여하려는 부스 ID
     * @return 참여 성공 시 true, 실패 시 false
     * @throws BaseException 부스가 존재하지 않거나 오류가 발생할 경우 예외 발생
     */
    public boolean joinBooth(String boothId) throws BaseException {
        String boothKey = BOOTH_PREFIX + boothId;

        // 부스가 존재하지 않을 경우 예외 발생
        if (Boolean.FALSE.equals(redisTemplate.hasKey(boothKey))) {
            throw new BaseException(BOOTH_NOT_FOUND);
        }

        // 현재 참가자 수와 최대 참가자 수를 가져옴
        int currentParticipants = Integer.parseInt(
                redisTemplate.opsForHash().get(boothKey, "currentParticipants").toString());
        int maxParticipants = Integer.parseInt(redisTemplate.opsForHash().get(boothKey, "maxParticipants").toString());

        // 부스가 이미 꽉 찬 경우
        if (currentParticipants >= maxParticipants) {
            // throw new BaseException(FULL_BOOTH_ERROR);
            return false;
        }

        // 참가자 수 증가
        redisTemplate.opsForHash().increment(boothKey, "currentParticipants", 1);
        return true;
    }

    /**
     * 부스 나가기
     *
     * @param boothId 나가려는 부스 ID
     * @param participantId 나가려는 참가자 ID
     * @throws BaseException 부스가 존재하지 않거나 오류가 발생할 경우 예외 발생
     */
    public void leaveBooth(String boothId, String participantId) throws BaseException {
        String boothKey = BOOTH_PREFIX + boothId;

        // 부스가 존재하지 않는 경우 예외 발생
        if (Boolean.FALSE.equals(redisTemplate.hasKey(boothKey))) {
            throw new BaseException(BOOTH_NOT_FOUND);
        }

        // 참가자 수 감소
        redisTemplate.opsForHash().increment(boothKey, "currentParticipants", -1);

        // 참가자가 0명이 되었다면 부스 삭제
        if ("0".equals(redisTemplate.opsForHash().get(boothKey, "currentParticipants"))) {
            redisTemplate.delete(boothKey);
        }
    }
}