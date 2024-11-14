package com.sparta.spotlightspacescheduler.core.calculation.service;

import static com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus.APPROVED;

import com.sparta.spotlightspacescheduler.core.calculation.domain.Calculation;
import com.sparta.spotlightspacescheduler.core.calculation.repository.CalculationRepository;
import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus;
import com.sparta.spotlightspacescheduler.core.payment.repository.PaymentRepository;
import com.sparta.spotlightspacescheduler.core.point.point.domain.Point;
import com.sparta.spotlightspacescheduler.core.point.point.repository.PointRepository;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculationService {

    private static final long ONE_MONTH = 1L;
    private static final String MONTHLY_AT_3_AM_ON_1ST = "0 0 3 1 * ?";

    private final CalculationRepository calculationRepository;
    private final PaymentRepository paymentRepository;
    private final PointRepository pointRepository;

    @Scheduled(cron = MONTHLY_AT_3_AM_ON_1ST)
    public void calculate() {
        LocalDateTime now = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        List<Payment> payments = paymentRepository.findPaymentsForCalculation(
                APPROVED,
                now.minusMonths(ONE_MONTH),
                now
        );

        List<User> users = payments.stream()
                .map(Payment::getEvent)
                .map(Event::getUser)
                .toList();

        Map<User, Point> pointMap = pointRepository.findPointsByUserIn(users).stream()
                .collect(Collectors.toMap(Point::getUser, point -> point));

        Map<User, Long> calculationMap = new HashMap<>();
        payments.forEach(payment -> {
            Event event = payment.getEvent();
            User user = event.getUser();
            Point point = pointMap.get(user);

            point.addPoint(payment.getOriginalAmount());
            event.calculate();
            calculationMap.put(
                    user,
                    calculationMap.getOrDefault(user, 0L) + payment.getOriginalAmount()
            );
        });

        List<Calculation> calculations = calculationMap.entrySet().stream()
                .map(entry -> Calculation.create(entry.getKey(), entry.getValue()))
                .toList();

        calculationRepository.saveAll(calculations);
    }
}
