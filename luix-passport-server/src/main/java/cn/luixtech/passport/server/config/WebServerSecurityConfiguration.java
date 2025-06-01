package cn.luixtech.passport.server.config;

import cn.luixtech.passport.server.config.oauth.handler.FederatedIdentityLoginSuccessHandler;
import cn.luixtech.passport.server.config.security.CsrfCookieFilter;
import cn.luixtech.passport.server.config.security.LuixCsrfRequestMatcher;
import cn.luixtech.passport.server.config.security.SpaCsrfTokenRequestHandler;
import cn.luixtech.passport.server.event.FederatedIdentityLoginSuccessEventListener;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Use org.springframework.security.crypto.password.DelegatingPasswordEncoder as default
 */
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
public class WebServerSecurityConfiguration {
    private static final String[]       PERMITTED_PAGES       = {
			"/",
			"/index.html",
            "/login",
			"/sign-in",
            "/sign-up",
            "/activate-account",
            "/forgot-password",
            "/reset-password",
			"/403",
			"/404",
			"/500",
			"/503",
			"/features",
			"/pricing",
			"/docs/**",
			"/terms-of-service",
			"/privacy-policy",
			"/contact-us",
    };
    public static final String[]       STATIC_RESOURCES    = {
//            "/static/**",
//            "/public/**",
//            "/resources/**",
//            "/css/**",
//            "/js/**",
//            "/images/**",
            "/favicon.ico",
            "/assets/**",
            "/webjars/**",
    };
    public static final String[]       MANAGEMENT_REQUESTS = {
            "/management/health/**",
            "/management/info/**",
    };
    private final       UserRepository userRepository;
    private final       UserService    userService;

//    /**
//     * Refer to <a href="https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html">Using a Custom Authorization Manager</a>
//     *
//     * @param manager DynamicAuthorizationManager
//     * @return Advisor
//     */
//    @Bean
//    public Advisor preAuthorize(@Lazy DynamicAuthorizationManager manager) {
//        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(manager);
//    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) ->
                web.ignoring()
                        // Remove below if remove H2
                        // requestMatchers("/h2-console/**") does NOT work, because there are query string in URL
                        // h2-console/login.do?jsessionid=f9c70ca0904f0960ff233ceca108853d
//                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
                        // Resolved issue of "Refused to display in a frame because it set 'X-Frame-Options' to 'deny'"
                        .requestMatchers("/swagger-ui/index.html");
    }

    // @formatter:off
	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, ApplicationProperties applicationProperties) throws Exception {
		http
			// Integrate both resource server and auth server
			.oauth2ResourceServer(server-> server.jwt(Customizer.withDefaults()))
			.authorizeHttpRequests(authorize ->
				authorize
					.requestMatchers(STATIC_RESOURCES).permitAll()
					.requestMatchers( PERMITTED_PAGES).permitAll()
					.requestMatchers(MANAGEMENT_REQUESTS).permitAll()
					.requestMatchers("/open-api/**").permitAll()
					.requestMatchers("/api/externals/authorities").hasAuthority("SCOPE_external:read")
//					.requestMatchers("/userinfo").hasAuthority("SCOPE_external:read")
					.requestMatchers("/swagger-ui/**").hasAuthority("ROLE_DEVELOPER")
					.requestMatchers("/v3/api-docs/**").hasAuthority("ROLE_DEVELOPER")
					.anyRequest().authenticated()
			)
			.csrf(csrf-> csrf
				// Single-Page Applications part of https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
					// Ignore matching requests
				.ignoringRequestMatchers("/open-api/**")
				// Solve post/delete forbidden issue for request from swagger
				.requireCsrfProtectionMatcher(new LuixCsrfRequestMatcher(applicationProperties.getAllowedCors().getMappings()))
			)
			.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
			.formLogin(formLogin ->
				formLogin
					.loginPage("/login")
			)
			.oauth2Login(oauth2Login ->
				oauth2Login
					.loginPage("/login")
					.successHandler(new FederatedIdentityLoginSuccessHandler(new FederatedIdentityLoginSuccessEventListener(userRepository, userService)))
			);
//			.headers(headers->headers.frameOptions(x->x.sameOrigin()));
		/*
		 * If you request POST /logout, then it will perform the following default operations using a series of LogoutHandlers:
		 *	Invalidate the HTTP session (SecurityContextLogoutHandler)
		 *	Clear the SecurityContextHolderStrategy (SecurityContextLogoutHandler)
		 *	Clear the SecurityContextRepository (SecurityContextLogoutHandler)
		 *	Clean up any RememberMe authentication (TokenRememberMeServices / PersistentTokenRememberMeServices)
		 *	Clear out any saved CSRF token (CsrfLogoutHandler)
		 *	Fire a LogoutSuccessEvent (LogoutSuccessEventPublishingLogoutHandler)
		 */
//			.logout(logout->
//				logout.logoutSuccessHandler(new FormLogoutSuccessHandler("/login?logout"))
//			);
		return http.build();
	}
	// @formatter:on

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
