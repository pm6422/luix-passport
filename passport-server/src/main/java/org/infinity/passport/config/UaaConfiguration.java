package org.infinity.passport.config;

import org.infinity.passport.config.oauth2.MongoApprovalStore;
import org.infinity.passport.config.oauth2.MongoAuthorizationCodeServices;
import org.infinity.passport.config.oauth2.MongoClientDetailsService;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.security.AjaxLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * OAuth 2.0 Grants list below
 * Authorization code grant
 * Password grant
 * Refresh token grant
 * Client credentials grant
 * Implicit grant
 * 
 *
 * Refer
 * http://spring.io/guides/tutorials/spring-boot-oauth2/
 * https://dazito.com/java/spring-boot-and-oauth2-with-jdbc
 * https://stackoverflow.com/questions/49197111/migration-to-spring-boot-2-security-encoded-password-does-not-look-like-bcrypt?noredirect=1&lq=1
 * http://projects.spring.io/spring-security-oauth/docs/oauth2.html
 * https://stackoverflow.com/questions/33812805/spring-security-oauth2-not-working-without-jwt
 * https://spring.io/guides/tutorials/spring-security-and-angular-js/#_oauth2_logout_angular_js_and_spring_security_part_ix
 * https://blog.csdn.net/a82793510/article/details/53509427
 * https://github.com/spring-guides/tut-spring-security-and-angular-js/issues/121
 * https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html
 * https://segmentfault.com/a/1190000012309216
 * https://alexbilbie.com/guide-to-oauth-2-grants/
 * https://github.com/keets2012/Auth-service
 */
@Configuration
public class UaaConfiguration {

    /**
     * Resource server is used to process API calls
     */
    @Configuration
    @EnableResourceServer
    @Import(SecurityProblemSupport.class)
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Autowired
        private SecurityProblemSupport   problemSupport;

        @Autowired
        private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .exceptionHandling()
                .accessDeniedHandler(problemSupport)
                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied"))
            .and()
                .formLogin() // formLogin should be explicitly declared here
            .and()
                .sessionManagement() // For authorize.html and AuthorizationEndpoint.java refer @SessionAttributes
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .and()
                .logout() // Logout is handled by LogoutFilter
                .logoutUrl("/api/account/logout") // For the logout behavior of OAuth2 password grant
                .logoutSuccessHandler(ajaxLogoutSuccessHandler) // It need to delete access token if u want to logout other clients simultaneously
            .and()
                .headers()
                .frameOptions()
                .disable()
            .and()
                .authorizeRequests()
                // Do not need authentication
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(EndpointRequest.to("health")).permitAll()
                // Need authentication
                .antMatchers("/api/**").authenticated()
                // Need 'DEVELOPER' authority
                .antMatchers("/v2/api-docs/**").hasAuthority(Authority.DEVELOPER)
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAuthority(Authority.DEVELOPER);
            // @formatter:on
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
        private MongoClientDetailsService      clientDetailsService;

        @Autowired
        private AuthenticationManager          authenticationManager;

        @Autowired
        private TokenStore                     tokenStore;

        @Autowired
        private MongoApprovalStore             approvalStore;

        @Autowired
        private UserDetailsService             userDetailsService;

        @Autowired
        private MongoAuthorizationCodeServices authorizationCodeServices;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            // Note: authenticationManager, tokenStore, userDetailsService must be injected here
            // 如果没有userDetailsService在使用refresh token刷新access token时报错
            // @formatter:off
            endpoints
                .authenticationManager(authenticationManager)
                .tokenStore(tokenStore)
                .approvalStore(approvalStore)
                .userDetailsService(userDetailsService)
                .authorizationCodeServices(authorizationCodeServices);
            // @formatter:on
            // Use to logout
            endpoints.addInterceptor(new HandlerInterceptorAdapter() {
                @Override
                public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                                       ModelAndView modelAndView) {
                    if (modelAndView != null && modelAndView.getView() instanceof RedirectView) {
                        RedirectView redirect = (RedirectView) modelAndView.getView();
                        String url = redirect.getUrl();
                        if (url.contains("code=") || url.contains("error=")) {
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                session.invalidate();
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
            // 如果没有下面一条语句会在使用authorization code获取access token时报Full
            // authentication is required to access this resource错误
            oauthServer.allowFormAuthenticationForClients();

            // 下面语句好像没起作用
            oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
        }
    }
}
