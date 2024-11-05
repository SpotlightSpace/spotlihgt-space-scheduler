package com.sparta.spotlightspacescheduler.core.ticket.dto.response;

import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.ticket.domain.Ticket;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TicketResponse {

    private final Long id;
    private final User user;
    private final Event event;
    private final boolean isCanceled;

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(ticket.getId(), ticket.getUser(), ticket.getEvent(), ticket.isCanceled());
    }
}
