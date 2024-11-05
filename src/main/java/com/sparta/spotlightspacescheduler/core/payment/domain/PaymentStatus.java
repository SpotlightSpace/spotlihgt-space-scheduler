package com.sparta.spotlightspacescheduler.core.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    PENDING("결제 보류"),
    READY("결제 준비"),
    APPROVED("결제 승인"),
    CANCELED("결제 취소"),
    FAILED("결제 실패");

    private final String description;

}
