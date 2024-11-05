package com.sparta.spotlightspacescheduler.core.event.dto.response;

import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.event.domain.EventCategory;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetEventResponseDto {
    private Long id;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int maxPeople;
    private int price;
    private EventCategory category;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentFinishAt;

    private GetEventResponseDto(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.content = event.getContent();
        this.location = event.getLocation();
        this.startAt = event.getStartAt();
        this.endAt = event.getEndAt();
        this.maxPeople = event.getMaxPeople();
        this.price = event.getPrice();
        this.category = event.getCategory();
        this.recruitmentStartAt = event.getRecruitmentStartAt();
        this.recruitmentFinishAt = event.getRecruitmentFinishAt();
    }

    public static GetEventResponseDto from(Event event) {
        return new GetEventResponseDto(event);
    }
}
