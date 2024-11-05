package com.sparta.spotlightspacescheduler.integration.slack;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlackEventListener {

    private final SlackApi slackApi;

    @EventListener(SlackEvent.class)
    public void sendMessage(SlackEvent event) {
        slackApi.sendMessage(event.getMessage());
    }
}
