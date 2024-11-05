package com.sparta.spotlightspacescheduler.core.user.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCalculateListResponseDto {

    private String name;
    private int totalPoints;

    private GetCalculateListResponseDto(String name, int totalPoints) {
        this.name = name;
        this.totalPoints = totalPoints;
    }

    public static GetCalculateListResponseDto of(String name, int totalPoints) {
        return new GetCalculateListResponseDto(name, totalPoints);
    }
}

