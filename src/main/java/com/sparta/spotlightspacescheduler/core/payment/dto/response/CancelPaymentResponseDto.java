package com.sparta.spotlightspacescheduler.core.payment.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@RequiredArgsConstructor
public class CancelPaymentResponseDto {

    /**
     * 요청 고유 번호
     */
    private final String aid;

    /**
     * 결제 고유 번호
     */
    private final String tid;

    /**
     * 가맹점 코드
     */
    private final String cid;

    /**
     * 결제 상태
     */
    private final String status;

    /**
     * 가맹점 주문번호
     */
    private final String partnerOrderId;

    /**
     * 가맹점 회원 id
     */
    private final String partnerUserId;

    /**
     * 결제 수단 (CARD 또는 MONEY 중 하나)
     */
    private final String paymentMethodType;

    /**
     * 결제 금액
     */
    private final Amount amount;

    /**
     * 이번 요청으로 취소된 금액
     */
    private final ApprovedCancelAmount approvedCancelAmount;

    /**
     * 누계 취소 금액
     */
    private final CanceledAmount canceledAmount;

    /**
     * 남은 취소 가능 금액
     */
    private final CancelAvailableAmount cancelAvailableAmount;

    /**
     * 상품 이름
     */
    private final String itemName;

    /**
     * 상품 코드
     */
    private final String itemCode;

    /**
     * 상품 수량
     */
    private final int quantity;

    /**
     * 결제 준비 요청 시각
     */
    private final LocalDateTime createdAt;

    /**
     * 결제 승인 시각
     */
    private final LocalDateTime approvedAt;

    /**
     * 결제 취소 시각
     */
    private final LocalDateTime canceledAt;

    /**
     * 결제 요청 시 전달한 값
     */
    private final String payload;

    @Getter
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Amount {

        /**
         * 전체 결제 금액
         */
        private final Integer total;

        /**
         * 비과세 금액
         */
        private final Integer taxFree;

        /**
         * 부가세 금액
         */
        private final Integer vat;

        /**
         * 사용한 포인트 금액
         */
        private final Integer point;

        /**
         * 할인 금액
         */
        private final Integer discount;

        /**
         * 컵 보증금
         */
        private final Integer greenDeposit;
    }

    @Getter
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ApprovedCancelAmount {

        /**
         * 이번 요청으로 취소된 전체 금액
         */
        private final int total;

        /**
         * 이번 요청으로 취소된 비과세 금액
         */
        private final int taxFree;

        /**
         * 이번 요청으로 취소된 부가세 금액
         */
        private final int vat;

        /**
         * 이번 요청으로 취소된 포인트 금액
         */
        private final int point;

        /**
         * 이번 요청으로 취소된 할인 금액
         */
        private final int discount;

        /**
         * 컵 보증금
         */
        private final int greenDeposit;
    }

    @Getter
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CanceledAmount {

        /**
         * 취소된 전체 누적 금액
         */
        private final int total;

        /**
         * 취소된 비과세 누적 금액
         */
        private final int taxFree;

        /**
         * 취소된 부가세 누적 금액
         */
        private final int vat;

        /**
         * 취소된 포인트 누적 금액
         */
        private final int point;

        /**
         * 취소된 할인 누적 금액
         */
        private final int discount;

        /**
         * 컵 보증금
         */
        private final int greenDeposit;
    }

    @Getter
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CancelAvailableAmount {

        /**
         * 전체 취소 가능 금액
         */
        private final int total;

        /**
         * 취소 가능 비과세 금액
         */
        private final int taxFree;

        /**
         * 취소 가능 부가세 금액
         */
        private final int vat;

        /**
         * 취소 가능 포인트 금액
         */
        private final int point;

        /**
         * 취소 가능 할인 금액
         */
        private final int discount;

        /**
         * 컵 보증금
         */
        private final int greenDeposit;
    }
}
