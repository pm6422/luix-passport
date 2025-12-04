package cn.luixtech.passport.server.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * OidcProviderConfigurationEndpointFilter
 */
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@Component
@WebFilter(filterName = "OpenIdConfigurationEndpointFilter", urlPatterns = "/.well-known/openid-configuration")
public class OpenIdConfigurationEndpointFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
        log.info("Accessed OpenIdConfigurationEndpointFilter");
    }
}