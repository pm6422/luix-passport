package org.infinity.passport.controller;

import org.infinity.passport.dto.TrackerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

import static org.infinity.passport.config.WebsocketConfiguration.IP_ADDRESS;

@Controller
public class TopicServerSubscriberController {
    private static final Logger                LOGGER = LoggerFactory.getLogger(TopicServerSubscriberController.class);
    @Autowired
    private              SimpMessagingTemplate messagingTemplate;


    //    @Override
    //    public void onApplicationEvent(SessionDisconnectEvent event) {
    //        ActivityDTO activityDTO = new ActivityDTO();
    //        activityDTO.setSessionId(event.getSessionId());
    //        activityDTO.setPage("logout");
    //        messagingTemplate.convertAndSend("/topic/client-subscriber/tracker", activityDTO);
    //    }

    @SubscribeMapping("/topic/server-subscriber/tracker")
    //    @SendTo("/topic/client-subscriber/tracker") @SendTo会被@Aspect拦截导致无效
    public void sendActivity(@Payload TrackerDTO activityDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) {
        activityDTO.setUserLogin(principal.getName());
        activityDTO.setSessionId(stompHeaderAccessor.getSessionId());
        activityDTO.setIpAddress(stompHeaderAccessor.getSessionAttributes().get(IP_ADDRESS).toString());
        activityDTO.setTime(Instant.now());
        LOGGER.debug("Sending user tracking data {}", activityDTO);
        messagingTemplate.convertAndSend("/topic/client-subscriber/tracker", activityDTO);
    }
}