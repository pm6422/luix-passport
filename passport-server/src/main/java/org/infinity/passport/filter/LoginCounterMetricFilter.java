package org.infinity.passport.filter;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.defaultString;

@WebFilter(filterName = "loginCounterMetricFilter", urlPatterns = "/oauth/token")
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class LoginCounterMetricFilter implements Filter {

    @Value("${spring.application.name}")
    private              String app;
    private static final String LOGINED_USER_TOTAL = ".logined.user.total";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(req, res);

        HttpServletResponse response = (HttpServletResponse) res;
        if (response.getStatus() == 200) {
//        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//        Tag tenantTag = Tag.of("tenant", MapUtils.isEmpty(pathVariables) ? StringUtils.EMPTY : defaultString(pathVariables.get("tenant-id")));
            Tag usernameTag = Tag.of("username", defaultString(req.getParameter("username")));
            // Note: Metrics of dropwizard does not have tag feature
            Metrics.counter(app.concat(LOGINED_USER_TOTAL), Tags.of(usernameTag)).increment();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}