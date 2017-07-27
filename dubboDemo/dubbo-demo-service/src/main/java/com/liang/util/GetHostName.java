package com.liang.util;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by setup on 2017/7/26.
 */
public class GetHostName {
    static String hostname = null;

    public static String get() {
        if (hostname == null) {
            hostname = System.getenv("COMPUTERNAME") != null ? System.getenv("COMPUTERNAME") : getHostNameForLiunx();
            Logger.getLogger(GetHostName.class).info("first get host name. hostname=" + hostname);
        }
        return hostname;
    }

    public static String getHostNameForLiunx() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException uhe) {
            String host = uhe.getMessage(); // host = "hostname: hostname"
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            return "UnknownHost";
        }
    }
}
