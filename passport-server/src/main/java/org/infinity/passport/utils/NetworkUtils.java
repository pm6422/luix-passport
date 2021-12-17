package org.infinity.passport.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Objects;

public abstract class NetworkUtils {
    /**
     * Intranet IP
     */
    public static final String INTRANET_IP = getIntranetIp();

    /**
     * Get intranet IP
     *
     * @return intranet IP
     */
    private static String getIntranetIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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