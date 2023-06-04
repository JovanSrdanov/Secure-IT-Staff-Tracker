package jass.security.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IPUtils {
    public static String getIPAddressFromHttpRequest(HttpServletRequest request) {
        String ipList = request.getHeader("X-Forwarded-For");
        if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
            return ipList.split(",")[0];
        }

        return request.getRemoteAddr();
    }
}
