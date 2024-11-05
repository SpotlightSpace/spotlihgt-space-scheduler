package com.sparta.spotlightspacescheduler.core.event.domain;

import com.sparta.spotlightspacescheduler.common.entity.Timestamped;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "events",
        indexes = {
                @Index(name = "idx_title_category", columnList = "title, category"),
                @Index(name = "idx_location_category", columnList = "location, category")
        })
public class Event extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(length = 100)
    private String title;

    @Column(length = 100)
    private String content;

    @Column(length = 100)
    private String location;

    // 시작 일시
    @Column(length = 50, name = "start_at")
    private LocalDateTime startAt;

    // 종료 일시
    @Column(length = 50, name = "end_at")
    private LocalDateTime endAt;

    @Column(length = 100, name = "max_people")
    private int maxPeople;

    @Column(length = 200)
    private int price;

    @Enumerated(EnumType.STRING)
    private EventCategory category;

    @Column
    private LocalDateTime recruitmentStartAt;

    @Column
    private LocalDateTime recruitmentFinishAt;

    @Column
    private boolean isDeleted = false;

    @Column
    private boolean isCalculated = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public boolean isFinishedRecruitment(LocalDateTime now) {
        return now.isAfter(recruitmentFinishAt);
    }

    public boolean isNotRecruitmentPeriod() {
        LocalDateTime now = LocalDateTime.now();
        return !(now.isAfter(recruitmentStartAt) && now.isBefore(recruitmentFinishAt));
    }

    public void calculate() {
        this.isCalculated = true;
    }
}
