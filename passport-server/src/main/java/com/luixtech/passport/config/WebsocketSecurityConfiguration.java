package com.luixtech.passport.config;

import com.luixtech.passport.domain.Authority;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebsocketSecurityConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        // @formatter:off
        messages
            .nullDestMatcher().authenticated()
            .simpDestMatchers("/topic/client-subscriber/app-health", "/topic/client-subscriber/tracker").hasAuthority(Authority.ADMIN)
                // matches any destination that starts with /topic/
                // (i.e. cannot send messages directly to /topic/)
                // (i.e. cannot subscribe to /topic/messages/* to get messages sent to
                // /topic/messages-user<id>)
            .simpDestMatchers("/topic/**").authenticated()
                // message types other than MESSAGE and SUBSCRIBE
            .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).denyAll()
                // catch all
            .anyMessage().denyAll();
        // @formatter:on
    }

    /**
     * Disables CSRF for Websockets.
     */
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}