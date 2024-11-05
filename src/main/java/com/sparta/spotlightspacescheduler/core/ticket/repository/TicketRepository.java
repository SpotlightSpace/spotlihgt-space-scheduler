package com.sparta.spotlightspacescheduler.core.ticket.repository;

import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.TICKET_NOT_FOUND;

import com.sparta.spotlightspacescheduler.common.exception.ApplicationException;
import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.ticket.domain.Ticket;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketQueryRepository {

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event.id = :eventId AND t.isCanceled = false")
    int countTicketByEvent(@Param("eventId") Long eventId);

    @Query("SELECT SUM(t.price) FROM Ticket t JOIN t.event e WHERE e.user.id = :userId AND e.isCalculated = false AND t.isCanceled = false")
    int findTotalAmountByUserId(@Param("userId") Long userId);

    Optional<Ticket> findFirstByUserAndEvent(User user, Event event);

    default Ticket findFirstByUserAndEventOrElseThrow(User user, Event event) {
        return findFirstByUserAndEvent(user, event).orElseThrow(() -> new ApplicationException(TICKET_NOT_FOUND));
    }
}
