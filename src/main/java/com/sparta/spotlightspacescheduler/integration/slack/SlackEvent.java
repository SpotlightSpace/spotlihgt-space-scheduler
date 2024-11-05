package com.sparta.spotlightspacescheduler.integration.slack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SlackEvent {

    private final String message;

    public static SlackEvent from(String message) {
        return new SlackEvent(message);
    }
}
