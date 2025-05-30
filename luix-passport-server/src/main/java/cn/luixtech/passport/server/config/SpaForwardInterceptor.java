package cn.luixtech.passport.server.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class SpaForwardInterceptor implements HandlerInterceptor {

    private static final List<String> STATIC_RESOURCE_EXTENSIONS = List.of(
            ".js", ".css", ".png", ".jpg", ".jpeg", ".gif", ".ico",
            ".woff", ".woff2", ".ttf", ".svg", ".json"
    );

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        String path = request.getRequestURI();

        if (STATIC_RESOURCE_EXTENSIONS.stream().anyMatch(path::endsWith)) {
            return true;
        }

        if (isAjaxRequest(request)) {
            return true;
        }
        // 其他请求转发到index.html
        if (!path.equals("/") && !path.equals("/index.html")) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return false;
        }
        return true;
    }

    public boolean isAjaxRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("application/json");
    }
}