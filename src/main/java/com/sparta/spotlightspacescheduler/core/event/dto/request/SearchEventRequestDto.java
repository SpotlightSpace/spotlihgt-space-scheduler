package com.sparta.spotlightspacescheduler.core.event.dto.request;

import com.sparta.spotlightspacescheduler.core.event.domain.EventCategory;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class SearchEventRequestDto {
    private String title;
    private Integer maxPeople;
    private String location;
    private EventCategory category;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentFinishAt;

    private SearchEventRequestDto(String title, Integer maxPeople, String location,
                                  EventCategory category,
                                  LocalDateTime recruitmentStartAt, LocalDateTime recruitmentFinishAt) {
        this.title = title;
        this.maxPeople = maxPeople;
        this.location = location;
        this.category = category;
        this.recruitmentStartAt = recruitmentStartAt;
        this.recruitmentFinishAt = recruitmentFinishAt;
    }

    public static SearchEventRequestDto of(String title, Integer maxPeople, String location,
                                           EventCategory category,
                                           LocalDateTime recruitmentStartAt, LocalDateTime recruitmentFinishAt) {
        return new SearchEventRequestDto(title, maxPeople, location, category, recruitmentStartAt, recruitmentFinishAt);
    }
}
