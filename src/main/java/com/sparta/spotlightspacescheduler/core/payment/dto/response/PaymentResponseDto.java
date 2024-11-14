package com.sparta.spotlightspacescheduler.core.payment.dto.response;

import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus;
import lombok.Getter;

@Getter
public class PaymentResponseDto {

    private long paymentId;
    private String tid;
    private String cid;
    private String eventTitle;
    private long eventId;
    private long userId;
    private int originalAmount;
    private int discountedAmount;
    private Long userCouponId;
    private Long pointId;
    private PaymentStatus status;

    private PaymentResponseDto(
            long paymentId,
            String tid,
            String cid,
            String eventTitle,
            long eventId,
            long userId,
            int originalAmount,
            int discountedAmount,
            Long userCouponId,
            Long pointId,
            PaymentStatus status
    ) {
        this.paymentId = paymentId;
        this.tid = tid;
        this.cid = cid;
        this.eventTitle = eventTitle;
        this.eventId = eventId;
        this.userId = userId;
        this.originalAmount = originalAmount;
        this.discountedAmount = discountedAmount;
        this.userCouponId = userCouponId;
        this.pointId = pointId;
        this.status = status;
    }

    public static PaymentResponseDto from(Payment payment) {
        Long userCouponId = payment.getUserCoupon() == null ? null : payment.getUserCoupon().getId();
        Long pointId = payment.getPoint() == null ? null : payment.getPoint().getId();

        return new PaymentResponseDto(
                payment.getId(),
                payment.getTid(),
                payment.getCid(),
                payment.getEvent().getTitle(),
                payment.getEvent().getId(),
                payment.getUser().getId(),
                payment.getOriginalAmount(),
                payment.getDiscountedAmount(),
                userCouponId,
                pointId,
                payment.getStatus()
        );
    }
}
