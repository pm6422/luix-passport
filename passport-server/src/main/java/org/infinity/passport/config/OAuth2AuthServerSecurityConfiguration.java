package org.infinity.passport.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.infinity.passport.config.oauth2.OAuth2ConfigurerUtils;
import org.infinity.passport.config.oauth2.passwordgrant.OAuth2PasswordAuthenticationConverter;
import org.infinity.passport.config.oauth2.passwordgrant.OAuth2PasswordAuthenticationProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
public class OAuth2AuthServerSecurityConfiguration {
    public static final String AUTHORIZATION_BEARER       = "Bearer ";
    public static final String AUTHORIZATION_BASIC        = "Basic ";
    public static final String INTERNAL_CLIENT_ID         = "messaging-client";
    public static final String AUTH_CODE_CLIENT_ID        = "login-client";
    public static final String INTERNAL_RAW_CLIENT_SECRET = "secret";
    public static final String TOKEN_URI                  = "/oauth2/token";
    public static final String INTROSPECT_TOKEN_URI       = "/oauth2/introspect";
    public static final String VIEW_JWK_URI               = "/oauth2/jwks";
    public static final String REVOKE_TOKEN_URI           = "/oauth2/revoke";
    public static final String CUSTOM_LOGIN_PAGE_URI      = "/oauth2/login";
    public static final String CUSTOM_CONSENT_PAGE_URI    = "/oauth2/consent";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      OAuth2PasswordAuthenticationProvider oAuth2PasswordAuthenticationProvider,
                                                                      DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer<>();
        // Apply authorization grant type authentication converters
        http.apply(authorizationServerConfigurer.tokenEndpoint(tokenEndpoint ->
                tokenEndpoint.accessTokenRequestConverter(getAuthorizationGrantTypeConverters())));
        // Specify the consent page URI
        authorizationServerConfigurer.authorizationEndpoint(endpoint -> endpoint.consentPage(CUSTOM_CONSENT_PAGE_URI));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests -> {
                    authorizeRequests.antMatchers(CUSTOM_LOGIN_PAGE_URI).permitAll();
                    authorizeRequests.anyRequest().authenticated();
                })
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .formLogin(Customizer.withDefaults())
                .apply(authorizationServerConfigurer);

        // Password grant authentication provider
        setPasswordGrantAuthentication(http, daoAuthenticationProvider, oAuth2PasswordAuthenticationProvider);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain standardSecurityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .oauth2ResourceServer(resource -> resource.jwt())
                .authorizeHttpRequests(authorize -> {
                    authorize.antMatchers(CUSTOM_LOGIN_PAGE_URI).permitAll();
                    authorize.antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    authorize.antMatchers("/app/**/*.{js,html}").permitAll();
                    authorize.antMatchers("/content/**").permitAll();
                    authorize.antMatchers("/favicon.png").permitAll();
                    authorize.antMatchers("/swagger-ui/index.html").permitAll();
                    authorize.anyRequest().authenticated();
                })
                .formLogin(Customizer.withDefaults());
        // @formatter:on
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        // @formatter:off
        RegisteredClient loginClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(AUTH_CODE_CLIENT_ID)
                .clientSecret(passwordEncoder().encode(INTERNAL_RAW_CLIENT_SECRET))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                // 最安全的流程，需要用户的参与
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // Supports multiple valid redirect URIs
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/login-client")
                .redirectUri("https://www.baidu.com")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
        RegisteredClient registeredClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(INTERNAL_CLIENT_ID)
                .clientSecret(passwordEncoder().encode(INTERNAL_RAW_CLIENT_SECRET))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                // 全局只有一个账号密码，使用这一个账号便可以访问资源，一般用于内部系统间调用
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                // 每个用户有不同的账号密码，每个用户可以使用自己的账号访问资源
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                // 根据refresh token可以重新生成access token
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope(OidcScopes.OPENID)
                .scope("message:read")
                .scope("message:write")
                .build();
        // @formatter:on
        return new InMemoryRegisteredClientRepository(loginClient, registeredClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // @formatter:off
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        // @formatter:on
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair keyPair) {
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().issuer("http://localhost:9070").build();
    }

    @Bean
    public OAuth2PasswordAuthenticationProvider oAuth2PasswordCredentialsAuthenticationProvider(PasswordEncoder passwordEncoder,
                                                                                                HttpSecurityBuilder httpSecurityBuilder,
                                                                                                UserDetailsService userDetailsService,
                                                                                                OAuth2AuthorizationService oAuth2AuthorizationService) {
        OAuth2TokenGenerator tokenGenerator = OAuth2ConfigurerUtils.getTokenGenerator(httpSecurityBuilder);
        return new OAuth2PasswordAuthenticationProvider(passwordEncoder, userDetailsService, oAuth2AuthorizationService, tokenGenerator);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder,
                                                               UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    private DelegatingAuthenticationConverter getAuthorizationGrantTypeConverters() {
        List<AuthenticationConverter> converters = Arrays.asList(
                // Client credentials grant type authentication converter
                new OAuth2ClientCredentialsAuthenticationConverter(),
                // Password grant type authentication converter
                new OAuth2PasswordAuthenticationConverter(),
                // Refresh token grant type authentication converter
                new OAuth2RefreshTokenAuthenticationConverter(),
                // Authorization code grant type authentication converter
                new OAuth2AuthorizationCodeAuthenticationConverter());
        return new DelegatingAuthenticationConverter(converters);
    }

    private static void setPasswordGrantAuthentication(HttpSecurity http,
                                                       DaoAuthenticationProvider daoAuthenticationProvider,
                                                       OAuth2PasswordAuthenticationProvider oAuth2PasswordAuthenticationProvider) {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(oAuth2PasswordAuthenticationProvider);
    }

//    @Bean
//    public TokenStore tokenStore() {
//        return new InMemoryTokenStore();
//    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // @formatter:off
        UserDetails userDetails = User
                .withUsername("user")
                .password("password")
                .passwordEncoder(passwordEncoder()::encode)
                .roles("USER")
                .build();
        // @formatter:on

        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}
