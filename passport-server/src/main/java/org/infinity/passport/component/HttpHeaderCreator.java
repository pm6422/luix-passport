package org.infinity.passport.component;

import lombok.AllArgsConstructor;
import org.infinity.passport.config.ApplicationConstants;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
public class HttpHeaderCreator {
    private final MessageSource messageSource;

    public HttpHeaders createSuccessHeader(String code, Object... args) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Success-Message", getMessage(code, args));
        return headers;
    }

    private String getMessage(String code, Object... args) {
        String message = messageSource.getMessage(code, args, ApplicationConstants.SYSTEM_LOCALE);
        try {
            message = URLEncoder.encode(message, StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            throw new RuntimeException("Cannot find the message code from message source properties.");
        }
        return message;
    }
}
