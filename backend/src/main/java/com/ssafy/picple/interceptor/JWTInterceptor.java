package com.ssafy.picple.interceptor;

import com.ssafy.picple.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JWTInterceptor implements HandlerInterceptor {

    private final String HEADER_AUTH = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        final String token = request.getHeader(HEADER_AUTH);

        if (token != null && JWTUtil.checkToken(token)) {
            return true;
        } else {
            throw new UnAuthorizedException();
        }

    }
}
