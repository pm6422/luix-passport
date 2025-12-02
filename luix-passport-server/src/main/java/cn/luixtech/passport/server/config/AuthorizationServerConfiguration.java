package cn.luixtech.passport.server.config;

import cn.luixtech.passport.server.config.oauth.DeviceClientAuthenticationConverter;
import cn.luixtech.passport.server.config.oauth.DeviceClientAuthenticationProvider;
import cn.luixtech.passport.server.config.oauth.authorization.token.LuixOAuth2TokenCustomizer;
import cn.luixtech.passport.server.utils.JwkUtils;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class AuthorizationServerConfiguration {
    public static final String                AUTHORIZATION_BEARER            = OAuth2AccessToken.TokenType.BEARER.getValue() + " ";
    public static final String                DEFAULT_PASSWORD_ENCODER_PREFIX = "{bcrypt}";
    public static final BCryptPasswordEncoder BCRYPT_PASSWORD_ENCODER         = new BCryptPasswordEncoder();
    public static final String                AUTHORIZATION_BASIC             = "Basic ";
    public static final String                AUTH_CODE_CLIENT_ID             = "messaging-client";
    public static final String                AUTH_CODE_CLIENT_SECRET         = "secret";
    public static final String                DEVICE_VERIFICATION_URI         = "/activate";
    public static final String                LOGIN_URI                       = "/login";
    /**
     * Refer to Endpoint {@link org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter}
     */
    public static final String                TOKEN_URI                       = "/oauth2/token";
    public static final String                INTROSPECT_TOKEN_URI            = "/oauth2/introspect";
    public static final String                VIEW_JWK_URI                    = "/oauth2/jwks";
    public static final String                REVOKE_TOKEN_URI                = "/oauth2/revoke";
    public static final String                CONSENT_PAGE_URI                = "/oauth2/consent";


    @Bean
    public AuthorizationServerSettings authorizationServerSettings(ApplicationProperties applicationProperties) {
        AuthorizationServerSettings.Builder builder = AuthorizationServerSettings.builder();
        if (applicationProperties.getCompany().isForceToHttps()) {
            builder.issuer(applicationProperties.getCompany().getDomain());
        }
        return builder.build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            RegisteredClientRepository registeredClientRepository,
            AuthorizationServerSettings authorizationServerSettings) throws Exception {
        http.securityMatcher(
                "/oauth2/**",
                "/.well-known/**",
                DEVICE_VERIFICATION_URI,
                VIEW_JWK_URI
        );

        /*
         * This sample demonstrates the use of a public client that does not
         * store credentials or authenticate with the authorization server.
         *
         * The following components show how to customize the authorization
         * server to allow for device clients to perform requests to the
         * OAuth 2.0 Device Authorization Endpoint and Token Endpoint without
         * a clientId/clientSecret.
         *
         * CAUTION: These endpoints will not require any authentication and can
         * be accessed by any client that has a valid clientId.
         *
         * It is therefore RECOMMENDED to carefully monitor the use of these
         * endpoints and employ any additional protections as needed, which is
         * outside the scope of this sample.
         */
        DeviceClientAuthenticationConverter deviceClientAuthenticationConverter =
                new DeviceClientAuthenticationConverter(
                        authorizationServerSettings.getDeviceAuthorizationEndpoint());
        DeviceClientAuthenticationProvider deviceClientAuthenticationProvider =
                new DeviceClientAuthenticationProvider(registeredClientRepository);

        http.oauth2AuthorizationServer(authorizationServer ->
                authorizationServer
                        .deviceAuthorizationEndpoint(endpoint -> endpoint.verificationUri(DEVICE_VERIFICATION_URI))
                        .deviceVerificationEndpoint(endpoint -> endpoint.consentPage(CONSENT_PAGE_URI))
                        .clientAuthentication(auth ->
                                auth
                                        .authenticationConverter(deviceClientAuthenticationConverter)
                                        .authenticationProvider(deviceClientAuthenticationProvider)
                        )
                        .authorizationEndpoint(endpoint -> endpoint.consentPage(CONSENT_PAGE_URI))
                        .oidc(Customizer.withDefaults())
        );

        // @formatter:off
		http
			.exceptionHandling((exceptions) -> exceptions
				.defaultAuthenticationEntryPointFor(
					new LoginUrlAuthenticationEntryPoint(LOGIN_URI),
					new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
				)
			)
			.oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()));
		// @formatter:on
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate,
                                                           RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate,
                                                                         RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> idTokenCustomizer() {
        return new LuixOAuth2TokenCustomizer();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = JwkUtils.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return NimbusJwtDecoder.withJwkSource(jwkSource).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
