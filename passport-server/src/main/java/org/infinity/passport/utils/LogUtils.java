package org.infinity.passport.utils;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.config.ApplicationConstants;
import org.infinity.passport.domain.Authority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class LogUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);

    public static void error(String message) {
        error(message, new Object[0]);
    }

    public static void error(String message, Object... arguments) {
        error(null, message, arguments);
    }

    public static void error(Throwable throwable, String message) {
        error(throwable, message, new Object[0]);
    }

    public static void error(Throwable throwable, String message, Object... arguments) {
        Assert.hasText(message, "it must not be null, empty, or blank");

        StringBuffer sb = new StringBuffer();
        sb.append("Error details:").append(IOUtils.LINE_SEPARATOR);
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            String profile = request.getServletContext().getInitParameter(ApplicationConstants.SPRING_PROFILES_ACTIVE);
            if (StringUtils.isNotEmpty(profile)) {
                sb.append(MessageFormat.format("Profile: {0}", profile)).append(IOUtils.LINE_SEPARATOR);
            }
        }
        String userName = SecurityUtils.getCurrentUserName() != null ? SecurityUtils.getCurrentUserName()
                : Authority.SYSTEM_ACCOUNT;
        sb.append(MessageFormat.format("User: {0}", userName)).append(IOUtils.LINE_SEPARATOR);
        sb.append(MessageFormat.format(message, arguments));
        if (throwable == null) {
            LOGGER.error(sb.toString());
        } else {
            LOGGER.error(sb.toString(), throwable);
        }
    }
}
