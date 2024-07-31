package com.ssafy.picple.domain.booth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoothCreateRequest {
    private String name;
    private int maxParticipants;
}