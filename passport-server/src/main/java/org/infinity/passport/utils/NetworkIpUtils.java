package org.infinity.passport.utils;

import java.net.InetAddress;

public class NetworkIpUtils {

    public static final String INTERNET_IP = getInternetIp(); // 外网IP

    private NetworkIpUtils() {
    }

    /**
     * 获得外网IP
     *
     * @return 外网IP
     */
    private static String getInternetIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException("Cannot acquire system IP!, please check network configuration on your server." + e.getMessage());
        }
    }
}