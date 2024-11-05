package com.sparta.spotlightspacescheduler.core.pointhistory.domain;

import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.point.point.domain.Point;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "point_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue
    @Column(name = "point_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    private Point point;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private int amount;

    @Enumerated(EnumType.STRING)
    private PointHistoryStatus status;

    private PointHistory(Point point, Payment payment, int amount) {
        this.point = point;
        this.payment = payment;
        this.amount = amount;
        this.status = PointHistoryStatus.USED;
    }

    public static PointHistory create(Payment payment, Point point, int amount) {
        return new PointHistory(point, payment, amount);
    }

    public void cancelPointUsage() {
        this.status = PointHistoryStatus.CANCELED;
    }
}
