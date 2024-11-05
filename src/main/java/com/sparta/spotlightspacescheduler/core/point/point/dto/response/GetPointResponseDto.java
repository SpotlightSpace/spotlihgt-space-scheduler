package com.sparta.spotlightspacescheduler.core.point.point.dto.response;

import com.sparta.spotlightspacescheduler.core.point.point.domain.Point;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetPointResponseDto {

    private Long id;
    private int amount;
    private String nickname;
    
    private GetPointResponseDto(Point point) {
        this.id = point.getId();
        this.amount = point.getAmount();
        this.nickname = point.getUser().getNickname();
    }
    
    public static GetPointResponseDto from(Point point) {
        return new GetPointResponseDto(point);
    }
}
