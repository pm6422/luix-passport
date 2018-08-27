package org.infinity.passport.controller;

import org.infinity.passport.dto.TrackerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.Instant;

import static org.infinity.passport.config.WebsocketConfiguration.IP_ADDRESS;

@Controller
public class TopicServerSubscriberController implements ApplicationListener<SessionDisconnectEvent> {
    private static final Logger                LOGGER = LoggerFactory.getLogger(TopicServerSubscriberController.class);
    @Autowired
    private              SimpMessagingTemplate messagingTemplate;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        TrackerDTO activityDTO = new TrackerDTO();
        activityDTO.setSessionId(event.getSessionId());
        activityDTO.setPage("logout");
        messagingTemplate.convertAndSend("/topic/client-subscriber/tracker", activityDTO);
    }

    @MessageMapping("/topic/server-subscriber/tracker")
    @SendTo("/topic/client-subscriber/tracker")
    public TrackerDTO sendActivity(@Payload TrackerDTO trackerDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) {
        trackerDTO.setUserLogin(principal.getName());
        trackerDTO.setSessionId(stompHeaderAccessor.getSessionId());
        trackerDTO.setIpAddress(stompHeaderAccessor.getSessionAttributes().get(IP_ADDRESS).toString());
        trackerDTO.setTime(Instant.now());
        LOGGER.debug("Sending user tracking data {}", trackerDTO);
        return trackerDTO;
    }
}