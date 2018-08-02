package org.infinity.passport.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This filter is used to calculate the execution time
 */
public class RequestExecutionTimeFilter implements Filter {

    private static final Logger LOGGER               = LoggerFactory.getLogger(RequestExecutionTimeFilter.class);

    private ThreadLocal<Long>   threadLocalStartTime = new ThreadLocal<Long>();

    @Override
    public void init(FilterConfig filterConfig) {
        // Nothing to initialize
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        threadLocalStartTime.set(System.currentTimeMillis());
        LOGGER.info("Processing request [{}] ==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>",
                ((HttpServletRequest) request).getRequestURI());
        chain.doFilter(request, response);
        if (threadLocalStartTime.get() != null) {
            long executionTime = System.currentTimeMillis() - threadLocalStartTime.get();
            threadLocalStartTime.remove();
            LOGGER.info("Processed request [{}] <==<==<==<==<==<==<==<==<==<==<==<==<==<==<==",
                    ((HttpServletRequest) request).getRequestURI());
            LOGGER.info("Processed request in {} ms", executionTime);
        }
    }
}
