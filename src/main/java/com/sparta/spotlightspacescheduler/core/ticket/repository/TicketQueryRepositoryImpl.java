package com.sparta.spotlightspacescheduler.core.ticket.repository;


import static com.sparta.spotlightspacescheduler.core.ticket.domain.QTicket.ticket;
import static com.sparta.spotlightspacescheduler.core.event.domain.QEvent.event;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spotlightspacescheduler.core.user.dto.response.GetCalculateListResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketQueryRepositoryImpl implements TicketQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<GetCalculateListResponseDto> findTotalAmountGroupedByEvent(Long userId) {
        return jpaQueryFactory
                .select(event.title, ticket.price.sum())
                .from(ticket)
                .join(ticket.event, event)
                .where(event.user.id.eq(userId)
                        .and(ticket.isCanceled.isFalse())
                        .and(event.isCalculated.isFalse()))
                .groupBy(event.id)
                .fetch()
                .stream()
                .map(tuple -> GetCalculateListResponseDto.of(
                        tuple.get(event.title),
                        tuple.get(ticket.price.sum()).intValue()))
                .collect(Collectors.toList());
    }
}
