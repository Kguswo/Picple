package com.ssafy.picple.domain.booth.controller;

import static com.ssafy.picple.config.baseresponse.BaseResponseStatus.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.picple.config.baseresponse.BaseException;
import com.ssafy.picple.config.baseresponse.BaseResponse;
import com.ssafy.picple.domain.booth.dto.BoothCreateRequest;
import com.ssafy.picple.domain.booth.service.BoothService;

import lombok.RequiredArgsConstructor;

/**
 * 부스 관련 API 요청을 처리
 * 클라이언트의 요청을 받아 Service와 상호작용 및 응답 반환
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booths")
public class BoothController {

    // 주입받아 부스 관련 비즈니스 로직 처리
    private final BoothService boothService;

    /**
     * 부스 생성 API
     *
     * @param request 부스 생성에 필요한 정보를 포함하는 요청 객체
     * @return 부스 생성에 성공 시 성공 상태를 포함한 BaseResponse 반환
     * @throws BaseException 부스 생성 중 오류 발생 시 예외 발생
     */
    @PostMapping
    public BaseResponse<Object> createBooth(@RequestBody BoothCreateRequest request) throws BaseException {

        // 부스 생성 로직 수행
        String boothId = boothService.createBooth(request.getName(), request.getMaxParticipants());

        // Service단에서 예외 처리를 하기 때문에 성공 반환
        return new BaseResponse<>(GENERATE_BOOTH_SUCCESSFULLY);
    }

    /**
     * 부스 참여 API
     *
     * @param boothId 참여하려는 부스 ID
     * @return 부스 참여에 성공 시 성공 상태를 포함한 BaseResponse 반환
     * @throws BaseException 부스 참여 중 오류 발생 시 예외 발생
     */
    @PostMapping("/{boothId}/join")
    public BaseResponse<Object> joinBooth(@PathVariable String boothId) throws BaseException {

        // 부스 참여 로직 수행
        boolean joined = boothService.joinBooth(boothId);

        // Service단에서 예외 처리를 하기 때문에 성공 반환
        return new BaseResponse<>(JOIN_BOOTH_SUCCESSFULLY);
    }

    /**
     * 부스 퇴장 API
     *
     * @param boothId 부스 ID
     * @param participantId 나가려는 참가자의 ID
     * @return 부스 퇴장에 성공 시 성공 상태를 포함한 BaseResponse 반환
     * @throws BaseException 부스 퇴장 중 오류 발생 시 예외 발생
     */
    @PostMapping("/{boothId}/leave")
    public BaseResponse<Object> leaveBooth(@PathVariable String boothId, @RequestParam String participantId) throws
            BaseException {

        // 부스 퇴장 로직 수행
        boothService.leaveBooth(boothId, participantId);

        // Service단에서 예외 처리를 하기 때문에 성공 반환
        return new BaseResponse<>(LEAVE_BOOTH_SUCCESSFULLY);
    }
}