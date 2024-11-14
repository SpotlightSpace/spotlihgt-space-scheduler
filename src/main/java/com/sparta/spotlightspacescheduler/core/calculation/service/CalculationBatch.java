package com.sparta.spotlightspacescheduler.core.calculation.service;

import com.sparta.spotlightspacescheduler.core.calculation.domain.Calculation;
import com.sparta.spotlightspacescheduler.core.calculation.dto.CalculationProcessResponseDto;
import com.sparta.spotlightspacescheduler.core.calculation.repository.CalculationRepository;
import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.event.repository.EventRepository;
import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.payment.domain.PaymentStatus;
import com.sparta.spotlightspacescheduler.core.point.point.domain.Point;
import com.sparta.spotlightspacescheduler.core.point.point.repository.PointRepository;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
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
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CalculationBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CalculationRepository calculationRepository;
    private final PointRepository pointRepository;
    private final EventRepository eventRepository;

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
            JdbcPagingItemReader<Payment> paymentReader,
            ItemProcessor paymentProcessor,
            ItemWriter paymentWriter
    ) {
        return new StepBuilder("calculationStep", jobRepository)
                .<Payment, Payment>chunk(30, transactionManager)
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
    public JdbcPagingItemReader<Payment> paymentReader(DataSource dataSource) {
        JdbcPagingItemReader<Payment> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(30);
        reader.setRowMapper(new PaymentRowMapper());

        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause(
                "SELECT p.payment_id, p.tid, p.cid, p.original_amount, " +
                        "e.event_id, e.start_at, e.end_at, e.price, e.recruitment_start_at, "
                        + "e.recruitment_finish_at, e.is_deleted AS event_is_deleted, e.is_calculated, "
                        + "u.user_id AS user_id"
        );

        queryProvider.setFromClause("FROM payments p " +
                "JOIN events e ON p.event_id = e.event_id " +
                "JOIN users u ON e.user_id = u.user_id ");

        queryProvider.setWhereClause(
                "WHERE p.status = :status " +
                        "AND :startInclusive <= e.end_at " +
                        "AND e.end_at < :endExclusive " +
                        "AND e.is_deleted = false");

        queryProvider.setSortKey("p.payment_id");

        try {
            reader.setQueryProvider(queryProvider.getObject());
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to initialize JdbcPagingItemReader", e);
        }

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("status", PaymentStatus.APPROVED.toString());
        parameterValues.put("startInclusive", LocalDateTime.now().minusMonths(1));
        parameterValues.put("endExclusive", LocalDateTime.now());
        reader.setParameterValues(parameterValues);

        return reader;
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
                    eventRepository.save(event);
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
