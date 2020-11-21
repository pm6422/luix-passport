package org.infinity.passport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * If any class extends WebSecurityConfigurerAdapter, the auto-configuration of spring security will don't work.
 * <p>
 * Refer
 * https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-Security-2.0
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public SecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        // @formatter:off
        web
                .ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**/*.{js,html}")
                .antMatchers("/content/**")
                .antMatchers("/open-api/**")
                .antMatchers("/favicon.png") // Note: it will cause authorization failure if loss this statement.
                .antMatchers("/swagger-ui/swagger-ui.html");
        // @formatter:on
    }

    /**
     * Note: csrf is activated by default when using WebSecurityConfigurerAdapter.
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http); // This statement is equivalent to below ones
        // @formatter:off
//        http
//            .formLogin()
//        .and()
//            .authorizeRequests()
//            .anyRequest().authenticated();
        // @formatter:on
    }
}
