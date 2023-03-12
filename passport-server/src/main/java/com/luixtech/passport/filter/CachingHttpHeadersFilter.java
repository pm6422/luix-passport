package com.luixtech.passport.filter;

import com.luixtech.framework.config.LuixProperties;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * This filter is used in production, to put HTTP cache headers with a long (1 month) expiration time.
 */
public class CachingHttpHeadersFilter implements Filter {

    /**
     * We consider the last modified date is the startup time of the server
     */
    private final static long           LAST_MODIFIED = System.currentTimeMillis();
    private final        LuixProperties luixProperties;
    private              long           expiredAfter;

    public CachingHttpHeadersFilter(LuixProperties luixProperties) {
        this.luixProperties = luixProperties;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        expiredAfter = TimeUnit.DAYS.toMillis(luixProperties.getHttp().getCache().getExpiredAfter());
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Cache-Control", "max-age=" + expiredAfter + ", public");
        httpResponse.setHeader("Pragma", "cache");
        // Setting Expires header, for proxy caching
        httpResponse.setDateHeader("Expires", System.currentTimeMillis() + expiredAfter);
        // Setting the Last-Modified header, for browser caching
        httpResponse.setDateHeader("Last-Modified", LAST_MODIFIED);

        chain.doFilter(request, response);
    }
}
