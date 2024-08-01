package com.ssafy.picple.domain.booth.service;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class BoothService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String BOOTH_PREFIX = "booth:";
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 10;

    public BoothService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String generateUniqueBoothId() {
        StringBuilder builder = new StringBuilder();
        SecureRandom random = new SecureRandom();

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

    // 부스(방) 생성 메서드
    public String createBooth(String name, int maxParticipants) {
        String boothId = generateUniqueBoothId();
        String boothKey = BOOTH_PREFIX + boothId;

        redisTemplate.opsForHash().put(boothKey, "name", name);
        redisTemplate.opsForHash().put(boothKey, "maxParticipants", String.valueOf(maxParticipants));
        redisTemplate.opsForHash().put(boothKey, "currentParticipants", "0");

        // 부스 정보를 2시간 동안 유지
        redisTemplate.expire(boothKey, 2, TimeUnit.HOURS);

        return boothId;
    }

    // 부스 참가 메서드
    public boolean joinBooth(String boothId) {
        String boothKey = BOOTH_PREFIX + boothId;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(boothKey))) {
            throw new IllegalArgumentException("Booth not found");
        }

        int currentParticipants = Integer.parseInt(redisTemplate.opsForHash().get(boothKey, "currentParticipants").toString());
        int maxParticipants = Integer.parseInt(redisTemplate.opsForHash().get(boothKey, "maxParticipants").toString());

        if (currentParticipants >= maxParticipants) {
            return false;
        }

        redisTemplate.opsForHash().increment(boothKey, "currentParticipants", 1);
        return true;
    }

    // 부스 나가기 메서드
    public void leaveBooth(String boothId, String participantId) {
        String boothKey = BOOTH_PREFIX + boothId;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(boothKey))) {
            throw new IllegalArgumentException("Booth not found");
        }

        redisTemplate.opsForHash().increment(boothKey, "currentParticipants", -1);

        // 만약 참가자가 0명이 되면 부스 삭제
        if ("0".equals(redisTemplate.opsForHash().get(boothKey, "currentParticipants"))) {
            redisTemplate.delete(boothKey);
        }
    }
}