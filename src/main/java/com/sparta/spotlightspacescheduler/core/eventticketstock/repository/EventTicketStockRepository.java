package com.sparta.spotlightspacescheduler.core.eventticketstock.repository;

import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.EVENT_TICKET_STOCK_NOT_FOUND;

import com.sparta.spotlightspacescheduler.common.exception.ApplicationException;
import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.eventticketstock.domain.EventTicketStock;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface EventTicketStockRepository extends JpaRepository<EventTicketStock, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EventTicketStock e where e.event.id = :eventId")
    Optional<EventTicketStock> findByEventIdWithPessimisticLock(long eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EventTicketStock> findByEvent(Event event);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EventTicketStock e where e.event in :events")
    List<EventTicketStock> findEventTicketStocksByEventIn(Set<Event> events);

    default EventTicketStock findByEventOrElseThrow(Event event) {
        return findByEvent(event).orElseThrow(() -> new ApplicationException(EVENT_TICKET_STOCK_NOT_FOUND));
    }

    default EventTicketStock findByEventIdWithPessimisticLockOrElseThrow(long eventId) {
        return findByEventIdWithPessimisticLock(eventId)
                .orElseThrow(() -> new ApplicationException(EVENT_TICKET_STOCK_NOT_FOUND));
    }
}
