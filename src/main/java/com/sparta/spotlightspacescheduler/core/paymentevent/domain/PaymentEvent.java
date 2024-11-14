package com.sparta.spotlightspacescheduler.core.paymentevent.domain;

import static com.sparta.spotlightspacescheduler.core.paymentevent.domain.PaymentEventType.APPROVE;
import static com.sparta.spotlightspacescheduler.core.paymentevent.domain.PaymentEventType.CANCEL;
import static com.sparta.spotlightspacescheduler.core.paymentevent.domain.PaymentEventType.READY;

import com.sparta.spotlightspacescheduler.common.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payment_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEvent extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_event_id")
    private Long id;

    private long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(length = 200)
    private PaymentEventType type;

    private PaymentEvent(long paymentId, PaymentEventType type) {
        this.paymentId = paymentId;
        this.type = type;
    }

    public static PaymentEvent createApproveEvent(long paymentId) {
        return new PaymentEvent(paymentId, APPROVE);
    }

    public static PaymentEvent createCancelEvent(long paymentId) {
        return new PaymentEvent(paymentId, CANCEL);
    }

    public static PaymentEvent createReadyEvent(long paymentId) {
        return new PaymentEvent(paymentId, READY);
    }
}
