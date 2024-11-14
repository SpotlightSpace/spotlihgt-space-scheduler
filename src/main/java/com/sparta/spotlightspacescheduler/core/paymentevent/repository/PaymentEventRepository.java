package com.sparta.spotlightspacescheduler.core.paymentevent.repository;

import com.sparta.spotlightspacescheduler.core.paymentevent.domain.PaymentEvent;
import com.sparta.spotlightspacescheduler.core.paymentevent.domain.PaymentEventType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, Long> {

    @Query("select pe from PaymentEvent pe join fetch Payment p on p.id = pe.paymentId where pe.type = :type and p.createAt < :createdAt")
    List<PaymentEvent> findAllByTypeAndCreatedAtBefore(PaymentEventType type, LocalDateTime createdAt);

    List<PaymentEvent> findAllByPaymentIdIn(List<Long> paymentIds);
}
