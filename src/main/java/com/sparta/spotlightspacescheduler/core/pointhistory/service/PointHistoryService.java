package com.sparta.spotlightspacescheduler.core.pointhistory.service;

import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.point.point.domain.Point;
import com.sparta.spotlightspacescheduler.core.pointhistory.domain.PointHistory;
import com.sparta.spotlightspacescheduler.core.pointhistory.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;

    public void createPointHistory(Payment payment, Point point, int amount) {
        PointHistory pointHistory = PointHistory.create(payment, point, amount);
        pointHistoryRepository.save(pointHistory);
    }
}
