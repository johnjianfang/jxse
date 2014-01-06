package org.apache.peer.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpUtils {

    final static  String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    final static Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);

    public static String getIpAddress(String ipStr) {
        Matcher matcher = pattern.matcher(ipStr);
        if (matcher.find()) {
            return matcher.group();
        }

        throw new RuntimeException("Invalid ip " + ipStr);
    }

    public static String getIpAddress(String ipStr, int last) {
        if (last < 0 || last > 254) {
            throw new RuntimeException("Invalid last IP " + last);
        }
        int inx = ipStr.lastIndexOf('.');
        return ipStr.substring(0, inx + 1) + last;
    }
}
