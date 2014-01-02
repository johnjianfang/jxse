package org.apache.peer.util;

public class StringUtils {

    public static String reverse(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return new StringBuffer(str).reverse().toString();
    }

    public static long reverse(long value) {

        return Long.parseLong(new StringBuffer().append(value).reverse().toString());
    }
}
