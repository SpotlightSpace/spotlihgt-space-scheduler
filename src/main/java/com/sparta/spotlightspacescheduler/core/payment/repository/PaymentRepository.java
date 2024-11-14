package com.sparta.spotlightspacescheduler.core.payment.repository;

import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.PAYMENT_NOT_FOUND;

import com.sparta.spotlightspacescheduler.common.exception.ApplicationException;
import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentQueryRepository {

    @Query("select p " +
            "from Payment p " +
            "join fetch p.event e " +
            "join fetch e.user " +
            "where p.status = :status and :startInclusive <= e.endAt and e.endAt < :endExclusive and e.isDeleted = false")
    List<Payment> findPaymentsForCalculation(
            @Param("status") PaymentStatus status,
            @Param("startInclusive") LocalDateTime startInclusive,
            @Param("endExclusive") LocalDateTime endExclusive
    );

    @Query("select p from Payment p where p.event = :event and p.status = :status")
    List<Payment> findPaymentsByEventAndStatus(@Param("event") Event event, @Param("status") PaymentStatus status);

    List<Payment> findAllByStatusAndUpdateAtBefore(PaymentStatus paymentStatus, LocalDateTime failDateTime);

    Optional<Payment> findByTid(String tid);

    Page<Payment> findAllByUserId(long userId, PageRequest pageRequest);

    @Query("select p from Payment p where p.id in :ids")
    List<Payment> findAllByIdIn(List<Long> ids);

    default Payment findByTidOrElseThrow(String tid) {
        return findByTid(tid).orElseThrow(() -> new ApplicationException(PAYMENT_NOT_FOUND));
    }

    @Query("select p from Payment p " +
            "join fetch p.event e " +
            "join fetch p.user " +
            "left join fetch p.userCoupon " +
            "left join fetch p.point " +
            "where p.id = :paymentId")
    Optional<Payment> findById(long paymentId);

    default Payment findByIdOrElseThrow(long paymentId) {
        return findById(paymentId).orElseThrow(() -> new ApplicationException(PAYMENT_NOT_FOUND));
    }

    List<Payment> findAllByEvent(Event event);
}
