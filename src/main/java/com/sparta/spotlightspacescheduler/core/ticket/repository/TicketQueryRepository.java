package com.sparta.spotlightspacescheduler.core.ticket.repository;

import com.sparta.spotlightspacescheduler.core.user.dto.response.GetCalculateListResponseDto;
import java.util.List;

public interface TicketQueryRepository {

    List<GetCalculateListResponseDto> findTotalAmountGroupedByEvent(Long userId);

}
