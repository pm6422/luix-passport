package org.infinity.passport.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
@Slf4j
public class PassportClientLauncher extends WebSecurityConfigurerAdapter {
    /**
     * Entrance method which used to run the application. Spring profiles can be configured with a program arguments
     * --spring.profiles.active=your-active-profile
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PassportClientLauncher.class, args);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf()
                .ignoringAntMatchers("/logout"); // Csrf should ignore logout url
//      .and()
//      .authorizeRequests()
//      .antMatchers("/index.html", "/home.html", "/", "/login").permitAll()
//      .anyRequest().authenticated() // IEngine need uncomment this statement
        // @formatter:on
    }
}
