package com.sparta.spotlightspacescheduler.core.point.point.dto.request;

import lombok.Getter;

@Getter
public class CreatePointRequestDto {
    private int price;

    private CreatePointRequestDto(int price) {
        this.price = price;
    }

    public static CreatePointRequestDto of(int price) {
        return new CreatePointRequestDto(price);
    }
}
