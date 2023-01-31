package com.luixtech.passport.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public abstract class NetworkUtils {
    public static String getRequestUrl(HttpServletRequest request) {
        Objects.requireNonNull(request);

        return request.getScheme() + // "http"
                "://" + // "://"
                request.getServerName() + // "host"
                ":" + // ":"
                request.getServerPort() + // "80"
                request.getContextPath();
    }
}