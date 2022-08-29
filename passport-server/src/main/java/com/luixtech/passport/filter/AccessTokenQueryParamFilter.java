package com.luixtech.passport.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Map;

@WebFilter(filterName = "accessTokenQueryParamFilter", urlPatterns = "/*")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AccessTokenQueryParamFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        SecurityContext context = SecurityContextHolder.getContext();
//        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
//            // do nothing
//        } else {
//            Map<String, String[]> params = req.getParameterMap();
//            if (!params.isEmpty() && params.containsKey("access_token")) {
//                String token = params.get("access_token")[0];
//                if (token != null) {
//                    Authentication auth = new TokenAuthentication(token);
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }
//            }
//        }
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}