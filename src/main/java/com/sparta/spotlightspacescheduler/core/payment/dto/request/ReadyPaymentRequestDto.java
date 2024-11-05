package com.sparta.spotlightspacescheduler.core.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ReadyPaymentRequestDto {

    @NotNull(message = "이벤트 ID는 필수 값입니다.")
    private Long eventId;

    private Long couponId;

    @PositiveOrZero(message = "포인트는 음수일 수 없습니다.")
    private Integer pointAmount;
}
