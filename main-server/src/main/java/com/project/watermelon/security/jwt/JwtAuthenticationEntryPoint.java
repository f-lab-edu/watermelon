package com.project.watermelon.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.watermelon.exception.ErrorCode;
import com.project.watermelon.exception.RestApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        RestApiException restApiException = new RestApiException();
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED_EXCEPTION;

        restApiException.setHttpStatus(errorCode.getHttpStatus());
        restApiException.setErrorMessage("Unauthorized");

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        String result = mapper.writeValueAsString(restApiException);
        response.getWriter().write(result);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
