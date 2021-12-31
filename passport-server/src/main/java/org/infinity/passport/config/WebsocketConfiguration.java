package org.infinity.passport.config;

import org.infinity.passport.domain.Authority;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    public static final String                IP_ADDRESS = "IP_ADDRESS";
    @Resource
    private             ApplicationProperties applicationProperties;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // @formatter:off
        String[] allowedOrigins = Optional
                .ofNullable(applicationProperties.getCors().getAllowedOrigins())
                .map(origins -> origins.toArray(new String[0]))
                .orElse(new String[0]);
        registry
                .addEndpoint("/websocket/app-health", "/websocket/tracker")
                .setAllowedOrigins(allowedOrigins)
                .setHandshakeHandler(defaultHandshakeHandler())
                .withSockJS()
                .setInterceptors(httpSessionHandshakeInterceptor());
        // @formatter:on
    }

    @Bean
    public HandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new HandshakeInterceptor() {

            @Override
            public boolean beforeHandshake(@Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response,
                                           @Nonnull WebSocketHandler wsHandler, @Nonnull Map<String, Object> attributes) {
                if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                    attributes.put(IP_ADDRESS, servletRequest.getRemoteAddress());
                }
                return true;
            }

            @Override
            public void afterHandshake(@Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response,
                                       @Nonnull WebSocketHandler wsHandler, Exception exception) {
            }
        };
    }

    private DefaultHandshakeHandler defaultHandshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(@Nonnull ServerHttpRequest request, @Nonnull WebSocketHandler wsHandler,
                                              @Nonnull Map<String, Object> attributes) {
                Principal principal = request.getPrincipal();
                if (principal == null) {
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority(Authority.ANONYMOUS));
                    principal = new AnonymousAuthenticationToken(WebsocketConfiguration.class.getSimpleName(),
                            "anonymous", authorities);
                }
                return principal;
            }
        };
    }
}
