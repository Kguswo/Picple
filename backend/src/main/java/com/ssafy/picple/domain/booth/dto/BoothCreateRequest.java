package com.ssafy.picple.domain.booth.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class BoothCreateRequest {
    private String name;
    private int maxParticipants;
}