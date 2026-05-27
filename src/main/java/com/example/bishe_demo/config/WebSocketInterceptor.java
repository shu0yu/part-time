package com.example.bishe_demo.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            String token = serverRequest.getServletRequest().getParameter("token");
            
            if (token != null && !token.isEmpty()) {
                try {
                    // 使用JwtUtil解析token
                    Map<String, Object> claims = com.example.bishe_demo.utils.JwtUtil.parseToken(token);
                    Long userId = null;
                    Object idObj = claims.get("id");
                    if (idObj instanceof Integer) {
                        userId = ((Integer) idObj).longValue();
                    } else if (idObj instanceof Long) {
                        userId = (Long) idObj;
                    }
                    if (userId != null) {
                        attributes.put("userId", userId);
                        System.out.println("WebSocket 握手成功，用户 ID: " + userId);
                        return true;
                    }
                } catch (Exception e) {
                    System.out.println("JWT 解析失败：" + e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
