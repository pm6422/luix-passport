package org.infinity.passport.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
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