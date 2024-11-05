package com.sparta.spotlightspacescheduler.core.payment.scheduler;

import static com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus.READY;

import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.eventticketstock.repository.EventTicketStockRepository;
import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.payment.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {

    private static final int ONE_MINUTE = 60_000;
    private static final long FIFTEEN_MINUTE = 15L;

    private final PaymentRepository paymentRepository;
    private final EventTicketStockRepository eventTicketStockRepository;

    @Transactional
    @Scheduled(fixedDelay = ONE_MINUTE)
    public void failPayment() {
        List<Payment> payments = paymentRepository.findAllByStatusAndUpdateAtBefore(
                READY,
                LocalDateTime.now().minusMinutes(FIFTEEN_MINUTE)
        );
        if (payments.isEmpty()) {
            return;
        }

        Map<Event, Long> eventPaymentCountMap = new HashMap<>();
        for (Payment payment : payments) {
            payment.fail();
            eventPaymentCountMap.put(
                    payment.getEvent(),
                    eventPaymentCountMap.getOrDefault(payment.getEvent(), 0L) + 1
            );
        }

        eventTicketStockRepository.findEventTicketStocksByEventIn(eventPaymentCountMap.keySet()).forEach(
                eventTicketStock ->
                        eventTicketStock.decreaseStock(eventPaymentCountMap.get(eventTicketStock.getEvent()))
        );
    }
}
