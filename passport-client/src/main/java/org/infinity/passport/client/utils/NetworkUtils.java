package org.infinity.passport.client.utils;

import java.net.InetAddress;

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
}