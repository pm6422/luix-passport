package org.infinity.passport.config;

import java.security.KeyPair;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import org.infinity.passport.security.AjaxLogoutSuccessHandler;
import org.infinity.passport.security.Http401UnauthorizedEntryPoint;
import org.infinity.passport.spi.model.Authority;

/**
 * Refer http://projects.spring.io/spring-security-oauth/docs/oauth2.html
 */
@Configuration
public class OAuth2ServerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2ServerConfiguration.class);

    @Value("${spring.application.name}" + "_")
    private String              appName;

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource          mysqlDataSource;

    @Bean
    public TokenStore tokenStore() {
        LOGGER.debug("Creating JDBC token store");
        JdbcTokenStore jdbcTokenStore = new JdbcTokenStore(mysqlDataSource);
        LOGGER.debug("Created JDBC token store");
        return jdbcTokenStore;
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        LOGGER.debug("Creating JDBC authorization code services");
        JdbcAuthorizationCodeServices authorizationCodeServices = new JdbcAuthorizationCodeServices(mysqlDataSource);
        LOGGER.debug("Created JDBC authorization code services");
        return authorizationCodeServices;
    }

    /**
     * Resource server负责处理API calls
     */
    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Autowired
        private Http401UnauthorizedEntryPoint authenticationEntryPoint;

        @Autowired
        private AjaxLogoutSuccessHandler      ajaxLogoutSuccessHandler;

        @Autowired
        private TokenStore                    tokenStore;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                    .logout()
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler(ajaxLogoutSuccessHandler)
                .and()
//                    .csrf().disable()
                    .headers().frameOptions().disable()
//                .and()
//                    .sessionManagement()
//                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                    // Do not need authentication
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .antMatchers("/swagger-resources/configuration/ui").permitAll()
                    .antMatchers("/management/health").permitAll()
                    // Need authentication
                    .antMatchers("/api/**").authenticated()
                    // Need 'DEVELOPER' authority
                    .antMatchers("/v2/api-docs/**").hasAuthority(Authority.DEVELOPER)
                    .antMatchers("/management/**").hasAuthority(Authority.DEVELOPER);
            // @formatter:on
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId("uaa").tokenStore(tokenStore);
        }
    }

    /**
     * Authorization server负责获取用户的授权并且发布token
     * AuthorizationServerEndpointsConfiguration
     */
    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthorizationCodeServices authorizationCodeServices;

        @Autowired
        private TokenStore                tokenStore;

        @Autowired
        private AuthenticationManager     authenticationManager;

        @Autowired
        private UserDetailsService        userDetailsService;

        @Autowired
        @Qualifier("mysqlDataSource")
        private DataSource                mysqlDataSource;

        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("config/keystore.jks"),
                    "foobar".toCharArray()).getKeyPair("test");
            converter.setKeyPair(keyPair);
            return converter;
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.jdbc(mysqlDataSource);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authorizationCodeServices(authorizationCodeServices).tokenStore(tokenStore)
            .authenticationManager(authenticationManager).userDetailsService(userDetailsService);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.allowFormAuthenticationForClients();
            // oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
        }

    }
}
