package cn.luixtech.passport.resourceserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class ResourceServerConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
		http
			.securityMatcher("/api/messages/**")
			.oauth2ResourceServer(server-> server.jwt(Customizer.withDefaults()))
			.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/messages/**").hasAuthority("SCOPE_all_supported_time_zone:read"));
		return http.build();
		// @formatter:on
    }
}
