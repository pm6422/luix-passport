package org.infinity.passport.controller;

import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.dto.TrackerDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.Objects;

import static org.infinity.passport.config.WebsocketConfiguration.IP_ADDRESS;

@Controller
@Slf4j
public class TopicServerSubscriberController {

    @MessageMapping("/topic/server-subscriber/tracker")
    @SendTo("/topic/client-subscriber/tracker")
    public TrackerDTO sendActivity(@Payload TrackerDTO trackerDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) {
        trackerDTO.setUserLogin(principal.getName());
        trackerDTO.setSessionId(stompHeaderAccessor.getSessionId());
        trackerDTO.setIpAddress(Objects.requireNonNull(stompHeaderAccessor.getSessionAttributes()).get(IP_ADDRESS).toString());
        trackerDTO.setTime(Instant.now());
        log.debug("Sending user tracking data {}", trackerDTO);
        return trackerDTO;
    }
}