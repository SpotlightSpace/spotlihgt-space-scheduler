package com.sparta.spotlightspacescheduler.core.calculation.service;

import static com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus.APPROVED;

import com.sparta.spotlightspacescheduler.core.calculation.domain.Calculation;
import com.sparta.spotlightspacescheduler.core.calculation.dto.CalculationProcessResponseDto;
import com.sparta.spotlightspacescheduler.core.calculation.repository.CalculationRepository;
import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus;
import com.sparta.spotlightspacescheduler.core.payment.repository.PaymentRepository;
import com.sparta.spotlightspacescheduler.core.point.point.domain.Point;
import com.sparta.spotlightspacescheduler.core.point.point.repository.PointRepository;
import com.sparta.spotlightspacescheduler.core.user.domain.User;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CalculationBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CalculationRepository calculationRepository;
    private final PaymentRepository paymentRepository;
    private final PointRepository pointRepository;
    private final EntityManagerFactory entityManagerFactory;

    // 배치의 실행 단위 - 정산 배치
    @Bean
    public Job calculationJob(Step calculationStep) {
        JobBuilder jobBuilder = new JobBuilder("CalculationJob", jobRepository);
        return jobBuilder.incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(calculationStep)
                .build();
    }

    // Job의 세부 실행 단위 - 한 달 전부터 1일까지 결제된 내역을 가져와서 계산
    @Bean
    @JobScope
    public Step calculationStep(JpaPagingItemReader<Payment> paymentJpaPagingItemReader,
            ItemProcessor<Payment, CalculationProcessResponseDto> paymentProcessor,
            ItemWriter<CalculationProcessResponseDto> paymentWriter) {
        return new StepBuilder("calculationStep", jobRepository)
                .<Payment, CalculationProcessResponseDto>chunk(30, transactionManager)
                .reader(paymentJpaPagingItemReader)
                .processor(paymentProcessor)
                .writer(paymentWriter)
                .faultTolerant()
                .retryPolicy(retryPolicy())
                .build();
    }

    // DB에서 데이터를 읽어오며 processor로 전달
    @Bean
    @StepScope
    public JpaPagingItemReader<Payment> paymentJpaPagingItemReader() {
        JpaPagingItemReader<Payment> jpaPagingItemReader = new JpaPagingItemReader<>();
        jpaPagingItemReader.setQueryString(
                "select p " +
                        "from Payment p " +
                        "join fetch p.event e " +
                        "join fetch e.user " +
                        "where p.status = :status " +
                        "and :startInclusive <= e.endAt " +
                        "and e.endAt < :endExclusive " +
                        "and e.isDeleted = false"
        );
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        jpaPagingItemReader.setPageSize(30);

        LocalDateTime now = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("status", PaymentStatus.APPROVED);
        parameterValues.put("startInclusive", now.minusMonths(1));
        parameterValues.put("endExclusive", now);
        jpaPagingItemReader.setParameterValues(parameterValues);

        return jpaPagingItemReader;
    }

    // 프로세서는 데이터를 가공하는 역할을 수행
    @Bean
    @StepScope
    public ItemProcessor<Payment, CalculationProcessResponseDto> paymentProcessor() {
        return new ItemProcessor<Payment, CalculationProcessResponseDto>() {
            @Override
            public CalculationProcessResponseDto process(Payment payment) throws Exception {
                Event event = payment.getEvent();
                User user = event.getUser();
                Point point = pointRepository.findByUserId(user.getId());
                return CalculationProcessResponseDto.of(user, point, event, payment.getOriginalAmount());
            }
        };
    }

    // 가공된 데이터를 저장하는 역할을 수행
    @Bean
    @StepScope
    public ItemWriter<CalculationProcessResponseDto> paymentWriter() {
        return new ItemWriter<CalculationProcessResponseDto>() {
            @Override
            public void write(Chunk<? extends CalculationProcessResponseDto> chunk) throws Exception {
                for (CalculationProcessResponseDto result : chunk.getItems()) {
                    Point point = result.getPoint();
                    Event event = result.getEvent();
                    point.addPoint(result.getOriginAmount());
                    event.calculate();
                    Calculation calculation = Calculation.create(result.getUser(), result.getOriginAmount());
                    calculationRepository.save(calculation);
                    pointRepository.save(point);
                }
            }
        };
    }

    // 재시도 정책 설정
    @Bean
    public RetryPolicy retryPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(TransientDataAccessException.class, true);
        return new SimpleRetryPolicy(5, exceptionClass);
    }
}
