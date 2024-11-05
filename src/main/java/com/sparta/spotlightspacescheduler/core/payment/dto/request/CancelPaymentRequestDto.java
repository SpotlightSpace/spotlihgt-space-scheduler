package com.sparta.spotlightspacescheduler.core.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CancelPaymentRequestDto {

    /**
     * 결제 고유 번호
     */
    @NotNull(message = "결제 ID 필수 값입니다.")
    private Long paymentId;
}
