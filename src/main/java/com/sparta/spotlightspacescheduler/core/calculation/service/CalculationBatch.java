package com.sparta.spotlightspacescheduler.core.calculation.service;

import static com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus.APPROVED;

import com.sparta.spotlightspacescheduler.core.calculation.domain.Calculation;
import com.sparta.spotlightspacescheduler.core.calculation.dto.CalculationProcessResponseDto;
import com.sparta.spotlightspacescheduler.core.calculation.repository.CalculationRepository;
import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.payment.repository.PaymentRepository;
import com.sparta.spotlightspacescheduler.core.point.point.domain.Point;
import com.sparta.spotlightspacescheduler.core.point.point.repository.PointRepository;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import java.time.LocalDateTime;
import java.util.Collections;
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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.domain.Sort;
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

    //배치의 실행 단위
    //정산 배치
    @Bean
    public Job calculationJob(Step calculationStep) {
        JobBuilder jobBuilder = new JobBuilder("CalculationJob", jobRepository);
        return jobBuilder
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(calculationStep)
                .build();
    }

    // job의 세부 실행단위.
    // 한달 전부터 1일까지 결제된 내역을 가져와서 계산
    @Bean
    @JobScope
    public Step calculationStep(
            RepositoryItemReader<Payment> paymentReader,
            ItemProcessor paymentProcessor,
            ItemWriter paymentWriter
    ) {
        return new StepBuilder("calculationStep", jobRepository)
                .<Payment, Payment>chunk(20000, transactionManager)
                .reader(paymentReader)
                .processor(paymentProcessor)
                .writer(paymentWriter)
                .faultTolerant()
                .retryPolicy(retryPolicy())
                .build();
    }

    // db에서 데이터를 읽어오며 porccesor로 전달.
    @Bean
    @StepScope
    public RepositoryItemReader<Payment> paymentReader() {

        LocalDateTime now = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        return new RepositoryItemReaderBuilder<Payment>()
                .name("paymentReader")
                .repository(paymentRepository)
                .methodName("findPaymentsForCalculation1")
                .arguments(APPROVED, now.minusMonths(1), now)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .pageSize(10000)
                .build();
    }

    // 프로세서는 가공하는 역할을 수행함
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

    // 가공된 데이터를 저장하는 역할을 수행함
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

    @Bean
    public RetryPolicy retryPolicy() {
        Map<Class<? extends Throwable>, Boolean> excepttionClass = new HashMap<>();
        excepttionClass.put(TransientDataAccessException.class, true);

        return new SimpleRetryPolicy(5, excepttionClass);
    }
}
