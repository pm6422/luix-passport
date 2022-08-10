package org.infinity.passport.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.infinity.passport.config.oauth2.FederatedIdentityConfigurer;
import org.infinity.passport.config.oauth2.OAuth2ConfigurerUtils;
import org.infinity.passport.config.oauth2.SecurityUserDetailsServiceImpl;
import org.infinity.passport.config.oauth2.UserRepositoryOAuth2UserHandler;
import org.infinity.passport.config.oauth2.passwordgrant.OAuth2PasswordAuthenticationConverter;
import org.infinity.passport.config.oauth2.passwordgrant.OAuth2PasswordAuthenticationProvider;
import org.infinity.passport.config.oauth2.repository.impl.MongoOAuth2RegisteredClientRepository;
import org.infinity.passport.repository.OAuth2ClientRepository;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.service.UserService;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
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
    public static final String AUTHORIZATION_BEARER    = "Bearer ";
    public static final String AUTHORIZATION_BASIC     = "Basic ";
    public static final String TOKEN_URI               = "/oauth2/token";
    public static final String INTROSPECT_TOKEN_URI    = "/oauth2/introspect";
    public static final String VIEW_JWK_URI            = "/oauth2/jwks";
    public static final String REVOKE_TOKEN_URI        = "/oauth2/revoke";
    public static final String CUSTOM_LOGIN_PAGE_URI   = "/oauth2/login";
    public static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      OAuth2PasswordAuthenticationProvider oAuth2PasswordAuthenticationProvider,
                                                                      DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer<>();
        // Apply authorization grant type authentication converters
        authorizationServerConfigurer.tokenEndpoint(endpoint -> endpoint.accessTokenRequestConverter(getAuthorizationGrantTypeConverters()));
        // Specify the consent page URI
        authorizationServerConfigurer.authorizationEndpoint(endpoint -> endpoint.consentPage(CUSTOM_CONSENT_PAGE_URI));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests -> {
                    authorizeRequests.antMatchers(CUSTOM_LOGIN_PAGE_URI).permitAll();
                    authorizeRequests.antMatchers("/open-api/**").permitAll();
                    authorizeRequests.antMatchers("/api/**").authenticated();
                })
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .formLogin(Customizer.withDefaults())
                .apply(authorizationServerConfigurer);
        // Configure custom login page URI
        http.apply(new FederatedIdentityConfigurer(CUSTOM_LOGIN_PAGE_URI));

        // Password grant authentication provider
        setPasswordGrantAuthentication(http, daoAuthenticationProvider, oAuth2PasswordAuthenticationProvider);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain standardSecurityFilterChain(HttpSecurity http) throws Exception {
        FederatedIdentityConfigurer federatedIdentityConfigurer = new FederatedIdentityConfigurer(CUSTOM_LOGIN_PAGE_URI);
        federatedIdentityConfigurer.oauth2UserHandler(new UserRepositoryOAuth2UserHandler());
        // @formatter:off
        http
                .oauth2ResourceServer(resource -> resource.jwt())
                .authorizeHttpRequests(authorize -> {
                    authorize.antMatchers(CUSTOM_LOGIN_PAGE_URI).permitAll();
                    authorize.antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    authorize.antMatchers("/").permitAll();
                    authorize.antMatchers("/app/**/*.{js,html}").permitAll();
                    authorize.antMatchers("/content/**").permitAll();
                    authorize.antMatchers("/favicon.png").permitAll();
                    authorize.antMatchers("/swagger-ui/index.html").permitAll();
                    authorize.antMatchers("/open-api/**").permitAll();
                    authorize.antMatchers("/api/**").authenticated();
                })
                .formLogin(Customizer.withDefaults())
                // Supports third-party login authentication
                .apply(federatedIdentityConfigurer);
        // @formatter:on
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService,
                                                 UserAuthorityRepository userAuthorityRepository) {
        return new SecurityUserDetailsServiceImpl(userService, userAuthorityRepository);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(OAuth2ClientRepository oauth2ClientRepository) {
        return new MongoOAuth2RegisteredClientRepository(oauth2ClientRepository);
    }

    @Bean
    public ProviderSettings providerSettings(ApplicationProperties applicationProperties) {
        return ProviderSettings.builder().issuer(applicationProperties.getServer().getAddress()).build();
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
                new OAuth2ClientCredentialsAuthenticationConverter(),
                new OAuth2PasswordAuthenticationConverter(),
                new OAuth2RefreshTokenAuthenticationConverter(),
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

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new InMemoryOAuth2AuthorizationService();
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
