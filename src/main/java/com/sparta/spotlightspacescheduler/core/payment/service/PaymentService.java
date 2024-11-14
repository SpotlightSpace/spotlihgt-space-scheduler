package com.sparta.spotlightspacescheduler.core.payment.service;

import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.CANCELLATION_PERIOD_EXPIRED;
import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.COUPON_ALREADY_USED;
import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.EVENT_TICKET_OUT_OF_STOCK;
import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.NOT_ENOUGH_POINT_AMOUNT;
import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.NOT_IN_EVENT_RECRUITMENT_PERIOD;

import com.sparta.spotlightspacescheduler.common.exception.ApplicationException;
import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.event.repository.EventRepository;
import com.sparta.spotlightspacescheduler.core.eventticketstock.domain.EventTicketStock;
import com.sparta.spotlightspacescheduler.core.eventticketstock.repository.EventTicketStockRepository;
import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.payment.dto.response.PaymentResponseDto;
import com.sparta.spotlightspacescheduler.core.payment.repository.PaymentRepository;
import com.sparta.spotlightspacescheduler.core.paymentevent.repository.PaymentEventRepository;
import com.sparta.spotlightspacescheduler.core.point.point.domain.Point;
import com.sparta.spotlightspacescheduler.core.point.point.repository.PointRepository;
import com.sparta.spotlightspacescheduler.core.pointhistory.domain.PointHistory;
import com.sparta.spotlightspacescheduler.core.pointhistory.repository.PointHistoryRepository;
import com.sparta.spotlightspacescheduler.core.ticket.service.TicketService;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import com.sparta.spotlightspacescheduler.core.user.repository.UserRepository;
import com.sparta.spotlightspacescheduler.core.usercoupon.domain.UserCoupon;
import com.sparta.spotlightspacescheduler.core.usercoupon.repository.UserCouponRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final TicketService ticketService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserCouponRepository userCouponRepository;
    private final PointRepository pointRepository;
    private final EventTicketStockRepository eventTicketStockRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PaymentEventRepository paymentEventRepository;

    public PaymentResponseDto getPayment(long paymentId) {
        return PaymentResponseDto.from(paymentRepository.findByIdOrElseThrow(paymentId));
    }

    public PaymentResponseDto getPayment(String tid) {
        return PaymentResponseDto.from(paymentRepository.findByTidOrElseThrow(tid));
    }

    public Page<PaymentResponseDto> getPayments(long userId, PageRequest pageRequest) {
        return paymentRepository.findAllByUserId(userId, pageRequest).map(PaymentResponseDto::from);
    }

    public long createPayment(long userId, long eventId, String cid, Long couponId, Integer pointAmount) {
        User user = userRepository.findByIdOrElseThrow(userId);
        Event event = eventRepository.findByIdOrElseThrow(eventId);
        EventTicketStock eventTicketStock = eventTicketStockRepository
                .findByEventIdWithPessimisticLockOrElseThrow(event.getId());

        validateRecruitmentPeriod(event);
        validateEventTicketStock(eventTicketStock);

        eventTicketStock.decreaseStock();

        UserCoupon userCoupon = null;
        pointAmount = pointAmount == null ? 0 : pointAmount;
        int discountedPrice = event.getPrice();
        if (doesCouponIdExist(couponId)) {
            userCoupon = userCouponRepository.findByCouponIdAndUserIdOrElseThrow(couponId, user.getId());
            validateUserCoupon(userCoupon);
            discountedPrice -= userCoupon.getDiscountAmount();
        }
        Point point = pointRepository.findByUserOrElseThrow(user);
        if (doesPointAmountExist(pointAmount)) {
            validatePoint(point, pointAmount);
            discountedPrice -= pointAmount;
        }

        Payment payment = Payment.create(
                cid,
                event,
                user,
                event.getPrice(),
                discountedPrice,
                userCoupon,
                point,
                pointAmount
        );
        paymentRepository.save(payment);

        return payment.getId();
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000),
            retryFor = TransientDataAccessException.class
    )
    public void readyPayment(long paymentId, String tid, long paymentEventId) {
        Payment payment = paymentRepository.findByIdOrElseThrow(paymentId);
        payment.ready(tid);

        paymentEventRepository.deleteById(paymentEventId);
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000),
            retryFor = TransientDataAccessException.class
    )
    public void approvePayment(long paymentId, long paymentEventId) {
        Payment payment = paymentRepository.findByIdOrElseThrow(paymentId);
        payment.approve();
        if (payment.isPointUsed()) {
            pointHistoryRepository.save(PointHistory.create(payment, payment.getPoint(), payment.getUsedPointAmount()));
        }

        ticketService.createTicket(payment.getUser(), payment.getEvent(), payment.getOriginalAmount());

        paymentEventRepository.deleteById(paymentEventId);
    }

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000),
            retryFor = TransientDataAccessException.class
    )
    public void cancelPayment(long paymentId, long paymentEventId) {
        Payment payment = paymentRepository.findByIdOrElseThrow(paymentId);
        Event event = payment.getEvent();

        if (event.isFinishedRecruitment(LocalDateTime.now())) {
            throw new ApplicationException(CANCELLATION_PERIOD_EXPIRED);
        }

        payment.cancel();
        if (payment.isPointUsed()) {
            PointHistory pointHistory = pointHistoryRepository.findByPaymentOrElseThrow(payment);
            pointHistory.cancelPointUsage();
        }

        EventTicketStock eventTicketStock = eventTicketStockRepository.findByEventOrElseThrow(payment.getEvent());
        eventTicketStock.increaseStock();

        ticketService.cancelTicket(payment.getUser(), payment.getEvent());

        paymentEventRepository.deleteById(paymentEventId);
    }

    public void cancelPayments(Event event) {
        List<Payment> payments = paymentRepository.findAllByEvent(event);
        payments.forEach(payment -> {
            payment.cancel();
            if (payment.isPointUsed()) {
                PointHistory pointHistory = pointHistoryRepository.findByPaymentOrElseThrow(payment);
                pointHistory.cancelPointUsage();
            }

            EventTicketStock eventTicketStock = eventTicketStockRepository.findByEventOrElseThrow(payment.getEvent());
            eventTicketStock.increaseStock();

            ticketService.cancelTicket(payment.getUser(), payment.getEvent());
        });
    }

    public void failPayment(long paymentId, long paymentEventId) {
        Payment payment = paymentRepository.findByIdOrElseThrow(paymentId);
        payment.fail();

        EventTicketStock eventTicketStock = eventTicketStockRepository.findByEventOrElseThrow(payment.getEvent());
        eventTicketStock.increaseStock();

        paymentEventRepository.deleteById(paymentEventId);
    }

    private boolean doesCouponIdExist(Long couponId) {
        return couponId != null;
    }

    private boolean doesPointAmountExist(int pointAmount) {
        return pointAmount > 0;
    }

    private void validateRecruitmentPeriod(Event event) {
        if (event.isNotRecruitmentPeriod()) {
            throw new ApplicationException(NOT_IN_EVENT_RECRUITMENT_PERIOD);
        }
    }

    private void validateEventTicketStock(EventTicketStock eventTicketStock) {
        if (eventTicketStock.isOutOfStock()) {
            throw new ApplicationException(EVENT_TICKET_OUT_OF_STOCK);
        }
    }

    private void validateUserCoupon(UserCoupon userCoupon) {
        if (userCoupon.isUsed()) {
            throw new ApplicationException(COUPON_ALREADY_USED);
        }
    }

    private void validatePoint(Point point, int pointAmount) {
        if (point.cannotDeduct(pointAmount)) {
            throw new ApplicationException(NOT_ENOUGH_POINT_AMOUNT);
        }
    }
}
