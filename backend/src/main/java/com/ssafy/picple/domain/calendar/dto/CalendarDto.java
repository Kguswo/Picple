package com.ssafy.picple.domain.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDto {
    private Long id;
    private String photoUrl;
    private String content;
}
