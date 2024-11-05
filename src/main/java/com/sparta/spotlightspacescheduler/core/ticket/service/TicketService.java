package com.sparta.spotlightspacescheduler.core.ticket.service;

import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.TICKET_PRICE_CANNOT_BE_NEGATIVE;

import com.sparta.spotlightspacescheduler.common.exception.ApplicationException;
import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.ticket.domain.Ticket;
import com.sparta.spotlightspacescheduler.core.ticket.dto.response.TicketResponse;
import com.sparta.spotlightspacescheduler.core.ticket.repository.TicketRepository;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketResponse createTicket(User user, Event event, int price) throws ApplicationException {
        if (isNegativePrice(price)) {
            throw new ApplicationException(TICKET_PRICE_CANNOT_BE_NEGATIVE);
        }

        Ticket ticket = Ticket.create(user, event, price);
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    public void cancelTicket(User user, Event event) {
        Ticket ticket = ticketRepository.findFirstByUserAndEventOrElseThrow(user, event);
        ticket.cancel();
    }

    private boolean isNegativePrice(int price) {
        return price < 0;
    }
}
