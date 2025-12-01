package cn.luixtech.passport.server.config.security;

import com.google.common.collect.ImmutableList;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Solve form csrf issue, add below
 * <th:input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
 */
@Slf4j
@AllArgsConstructor
public class LuixCsrfRequestMatcher implements RequestMatcher {
    private static final Pattern             ALLOWED_METHODS = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
    private static final List<String>        IGNORED_PATHS   = ImmutableList.of("swagger-ui/index.html");
    private final        Map<String, String> allowedMappings;

    @Override
    public boolean matches(HttpServletRequest request) {
        // CSRF disabled on GET, HEAD, TRACE, OPTIONS (i.e. enabled for POST, PUT, DELETE)
        if (ALLOWED_METHODS.matcher(request.getMethod()).matches()) {
            return false;
        }

        final String referer = request.getHeader("Referer");
        log.info("Request referer: {}", referer);

        boolean allowedMappingFound = allowedMappings.entrySet()
                .stream()
                .anyMatch(entry -> entry.getKey().equals(request.getRequestURI()) && referer.contains(entry.getValue()));
        if (allowedMappingFound) {
            return false;
        }

        if (referer != null && IGNORED_PATHS.stream().anyMatch(referer::contains)) {
            return false;
        }
        // otherwise, CSRF is required
        return true;
    }
}