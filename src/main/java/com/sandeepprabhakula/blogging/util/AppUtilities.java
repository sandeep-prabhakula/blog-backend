package com.sandeepprabhakula.blogging.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class AppUtilities {
    public String getClientIPAddr(ServerHttpRequest request){
        String userIp = request.getHeaders().getFirst("X-Forwarded-For");
        if (userIp == null || userIp.isEmpty()) {
            userIp = request.getRemoteAddress() != null
                    ? request.getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
        }
        return userIp.split(",")[0].trim();
    }
}
