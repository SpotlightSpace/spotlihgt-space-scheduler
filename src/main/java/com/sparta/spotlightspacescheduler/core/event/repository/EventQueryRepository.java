package com.sparta.spotlightspacescheduler.core.event.repository;

import com.sparta.spotlightspacescheduler.core.event.dto.request.SearchEventRequestDto;
import com.sparta.spotlightspacescheduler.core.event.dto.response.GetEventResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventQueryRepository {

    Page<GetEventResponseDto> searchEvents(SearchEventRequestDto requestDto, String type, Pageable pageable);

}
