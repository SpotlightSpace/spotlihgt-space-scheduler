package com.sparta.spotlightspacescheduler.core.calculation.repository;

import com.sparta.spotlightspacescheduler.core.calculation.domain.Calculation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalculationRepository extends JpaRepository<Calculation, Long> {

}
