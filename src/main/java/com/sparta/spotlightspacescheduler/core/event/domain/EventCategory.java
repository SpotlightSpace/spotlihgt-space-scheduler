package com.sparta.spotlightspacescheduler.core.event.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventCategory {

    MUSIC("MUSIC"),
    CONCERT("CONCERT"),
    ART("ART"),
    MOVIE("MOVIE"),
    COMMUNITY("COMMUNITY"),
    WORKSHOP("WORKSHOP");



    private final String categoryRole;

}
