package cn.luixtech.passport.client.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
public class WebServerSecurityConfiguration {
    private final ClientRegistrationRepository clientRegistrationRepository;

    // @formatter:off
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize ->
				authorize
					.requestMatchers("/webjars/**").permitAll()
					.requestMatchers("favicon.ico").permitAll()
					.requestMatchers("/assets/**").permitAll()
					.requestMatchers("/logged-out").permitAll()
					.requestMatchers("/management/health/**").permitAll()
					.anyRequest().authenticated()
			)
			// Default OAuth 2.0 Login Page should match the format /oauth2/authorization/{registrationId}
			// See {@link org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI}
			.oauth2Login(oauth2Login -> oauth2Login.loginPage("/oauth2/authorization/messaging-client-oidc"))
			.oauth2Client(withDefaults())
			.logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler()));
		return http.build();
	}
	// @formatter:on

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

        // Set the location that the End-User's User Agent will be redirected to
        // after the logout has been performed at the Provider
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/logged-out");
        return oidcLogoutSuccessHandler;
    }
}
