package com.sparta.spotlightspacescheduler.core.calculation.dto;

import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.point.point.domain.Point;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import lombok.Getter;

@Getter
public class CalculationProcessResponseDto {
    private final User user;
    private final Point point;
    private final Event event;
    private final int originAmount;

    private CalculationProcessResponseDto(User user, Point point, Event event, int originAmount) {
        this.user = user;
        this.point = point;
        this.event = event;
        this.originAmount = originAmount;
    }

    public static CalculationProcessResponseDto of(User user, Point point, Event event, int originAmount) {
        return new CalculationProcessResponseDto(user, point, event, originAmount);
    }
}
