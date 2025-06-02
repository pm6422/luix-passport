package cn.luixtech.passport.server.interceptor;

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

        // Allow non-GET requests to proceed
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Allow static resources to proceed
        if (STATIC_RESOURCE_EXTENSIONS.stream().anyMatch(path::endsWith)) {
            return true;
        }

        // Allow JSON requests to proceed
        if (isJsonRequest(request)) {
            return true;
        }

        // Forward non-static, non-AJAX GET requests to index.html (except for "/" and "/index.html")
        if (!path.equals("/") && !path.equals("/index.html")) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return false;
        }
        return true;
    }

    public boolean isJsonRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("application/json");
    }
}