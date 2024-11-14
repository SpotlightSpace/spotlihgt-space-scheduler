package com.sparta.spotlightspacescheduler.core.payment.dto.response.kakaopay;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaopayPaymentStatusResponseDto {


    /**
     * 결제 고유번호
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
     * 결제 수단, CARD 또는 MONEY 중 하나
     */
    private final String paymentMethodType;

    /**
     * 결제 금액
     */
    private final Amount amount;

    /**
     * 취소된 금액
     */
    private final CanceledAmount canceledAmount;

    /**
     * 취소 가능 금액
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
    private final Integer quantity;

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
     * 결제 카드 정보
     */
    private final SelectedCardInfo selectedCardInfo;

    /**
     * 결제/취소 상세
     */
    private final List<PaymentActionDetail> paymentActionDetails;

    @Getter
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Amount {

        /**
         * 전체 결제 금액
         */
        private final int total;

        /**
         * 비과세 금액
         */
        private final int taxFree;

        /**
         * 부가세 금액
         */
        private final int vat;

        /**
         * 사용한 포인트 금액
         */
        private final int point;

        /**
         * 할인 금액
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
         * 전체 취소 금액
         */
        private final int total;

        /**
         * 취소된 비과세 금액
         */
        private final int taxFree;

        /**
         * 취소된 부가세 금액
         */
        private final int vat;

        /**
         * 취소된 포인트 금액
         */
        private final int point;

        /**
         * 취소된 할인 금액
         */
        private final int discount;

        /**
         * 취소된 컵 보증금
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
         * 취소 가능한 비과세 금액
         */
        private final int taxFree;

        /**
         * 취소 가능한 부가세 금액
         */
        private final int vat;

        /**
         * 취소 가능한 포인트 금액
         */
        private final int point;

        /**
         * 취소 가능한 할인 금액
         */
        private final int discount;

        /**
         * 취소 가능한 컵 보증금
         */
        private final int greenDeposit;
    }

    @Getter
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SelectedCardInfo {

        /**
         * 카드 BIN
         */
        private final String cardBin;

        /**
         * 할부 개월 수
         */
        private final Integer installMonth;

        /**
         * 할부 유형, (CARD_INSTALLMENT: 업종 무이자, SHARE_INSTALLMENT: 분담 무이자)
         */
        private final String installmentType;

        /**
         * 카드사 정보
         */
        private final String cardCorpName;

        /**
         * 무이자할부 여부 (Y/N)
         */
        private final String interestFreeInstall;
    }

    @Getter
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PaymentActionDetail {

        /**
         * 요청 고유 번호
         */
        private final String aid;

        /**
         * 거래 시간
         */
        private final LocalDateTime approvedAt;

        /**
         * 결제/취소 총액
         */
        private final Integer amount;

        /**
         * 결제/취소 포인트 금액
         */
        private final Integer pointAmount;

        /**
         * 할인 금액
         */
        private final Integer discountAmount;

        /**
         * 컵 보증금
         */
        private final Integer greenDeposit;

        /**
         * 결제 타입, (PAYMENT: 결제, CANCEL: 결제 취소, ISSUED_SID: SID 발급)
         */
        private final String paymentActionType;

        /**
         * Request로 전달한 값
         */
        private final String payload;
    }
}

