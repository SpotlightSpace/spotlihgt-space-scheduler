package com.sparta.spotlightspacescheduler.core.paymentevent.scheduler;

import static com.sparta.spotlightspacescheduler.core.paymentevent.domain.PaymentEventType.APPROVE;
import static com.sparta.spotlightspacescheduler.core.paymentevent.domain.PaymentEventType.CANCEL;
import static com.sparta.spotlightspacescheduler.core.paymentevent.domain.PaymentEventType.READY;

import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.payment.dto.response.kakaopay.KakaopayPaymentStatusResponseDto;
import com.sparta.spotlightspacescheduler.core.payment.repository.PaymentRepository;
import com.sparta.spotlightspacescheduler.core.payment.service.PaymentService;
import com.sparta.spotlightspacescheduler.core.paymentevent.domain.PaymentEvent;
import com.sparta.spotlightspacescheduler.core.paymentevent.repository.PaymentEventRepository;
import com.sparta.spotlightspacescheduler.integration.kakaopay.KakaopayApi;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventScheduler {

    private static final int ONE_MINUTE = 60_000;

    private final PaymentEventRepository paymentEventRepository;
    private final PaymentService paymentService;
    private final KakaopayApi kakaopayApi;
    private final PaymentRepository paymentRepository;

    @Scheduled(fixedDelay = ONE_MINUTE)
    public void handleReadyTypePaymentEvent() {
        List<PaymentEvent> paymentEvents =
                paymentEventRepository.findAllByTypeAndCreatedAtBefore(READY, LocalDateTime.now().minusMinutes(1));
        List<Long> paymentIds = paymentEvents.stream()
                .map(PaymentEvent::getPaymentId)
                .toList();
        List<Payment> payments = paymentRepository.findAllByIdIn(paymentIds);

        Map<Long, PaymentEvent> paymentEventMap = paymentEvents.stream()
                .collect(Collectors.toMap(PaymentEvent::getPaymentId, paymentEvent -> paymentEvent));

        payments.forEach(payment ->
                paymentService.failPayment(payment.getId(), paymentEventMap.get(payment.getId()).getId())
        );
    }

    @Scheduled(fixedDelay = ONE_MINUTE)
    public void handleApproveTypePaymentEvent() {
        List<PaymentEvent> paymentEvents =
                paymentEventRepository.findAllByTypeAndCreatedAtBefore(APPROVE, LocalDateTime.now().minusMinutes(1));
        List<Long> paymentIds = paymentEvents.stream()
                .map(PaymentEvent::getPaymentId)
                .toList();
        List<Payment> payments = paymentRepository.findAllByIdIn(paymentIds);

        Map<Long, PaymentEvent> paymentEventMap = paymentEvents.stream()
                .collect(Collectors.toMap(PaymentEvent::getPaymentId, paymentEvent -> paymentEvent));

        payments.forEach(payment -> {
                    KakaopayPaymentStatusResponseDto kakaopayPaymentStatus
                            = kakaopayApi.getPaymentStatus(payment.getCid(), payment.getTid());
                    if (kakaopayPaymentStatus.getStatus().equals("SUCCESS_PAYMENT")) {
                        paymentService.approvePayment(payment.getId(), paymentEventMap.get(payment.getId()).getId());
                    } else {
                        paymentService.failPayment(payment.getId(), paymentEventMap.get(payment.getId()).getId());
                    }
                }
        );
    }

    @Scheduled(fixedDelay = ONE_MINUTE)
    public void handleCancelTypePaymentEvent() {
        List<PaymentEvent> paymentEvents =
                paymentEventRepository.findAllByTypeAndCreatedAtBefore(CANCEL, LocalDateTime.now().minusMinutes(1));
        List<Long> paymentIds = paymentEvents.stream()
                .map(PaymentEvent::getPaymentId)
                .toList();
        List<Payment> payments = paymentRepository.findAllByIdIn(paymentIds);

        Map<Long, PaymentEvent> paymentEventMap = paymentEvents.stream()
                .collect(Collectors.toMap(PaymentEvent::getPaymentId, paymentEvent -> paymentEvent));

        payments.forEach(payment -> {
                    KakaopayPaymentStatusResponseDto kakaopayPaymentStatus
                            = kakaopayApi.getPaymentStatus(payment.getCid(), payment.getTid());
                    if (kakaopayPaymentStatus.getStatus().equals("CANCEL_PAYMENT")) {
                        paymentService.cancelPayment(payment.getId(), paymentEventMap.get(payment.getId()).getId());
                    }
                }
        );
    }
}
