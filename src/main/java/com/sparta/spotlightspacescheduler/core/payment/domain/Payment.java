package com.sparta.spotlightspacescheduler.core.payment.domain;

import static com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus.APPROVED;
import static com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus.CANCELED;
import static com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus.PENDING;
import static com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus.READY;

import com.sparta.spotlightspacescheduler.common.entity.Timestamped;
import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.point.point.domain.Point;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import com.sparta.spotlightspacescheduler.core.usercoupon.domain.UserCoupon;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends Timestamped {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String tid;

    @Column(nullable = false)
    private String cid;

    @JoinColumn(name = "event_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private int originalAmount;

    private int discountedAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id")
    private UserCoupon userCoupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    private Point point;

    private int usedPointAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Payment(
            String tid,
            String cid,
            Event event,
            User user,
            int originalAmount,
            int discountedAmount,
            UserCoupon userCoupon,
            Point point,
            int pointAmount,
            PaymentStatus status
    ) {
        this.tid = tid;
        this.cid = cid;
        this.event = event;
        this.user = user;
        this.originalAmount = originalAmount;
        this.discountedAmount = discountedAmount;
        this.userCoupon = userCoupon;
        this.point = point;
        this.usedPointAmount = pointAmount;
        this.status = status;
    }

    public static Payment create(
            String cid,
            Event event,
            User user,
            int originalAmount,
            int discountedAmount,
            UserCoupon userCoupon,
            Point point,
            int pointAmount
    ) {
        return new Payment(null, cid, event, user, originalAmount, discountedAmount, userCoupon, point, pointAmount,
                PENDING);
    }

    public void approve() {
        this.status = APPROVED;
        this.point.deduct(this.usedPointAmount);
        if (this.userCoupon != null) {
            this.userCoupon.use();
        }
    }

    public void cancel() {
        this.status = CANCELED;
        this.point.cancelUsage(this.usedPointAmount);
        if (this.userCoupon != null) {
            this.userCoupon.cancelUsage();
        }
    }

    public boolean isPointUsed() {
        return this.usedPointAmount != 0;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void ready(String tid) {
        this.tid = tid;
        this.status = READY;
    }
}
