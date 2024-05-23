package com.project.watermelon.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.watermelon.exception.ErrorCode;
import com.project.watermelon.exception.RestApiExceptionInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 필요한 권한이 없이 접근하려 할때 403
        RestApiExceptionInfo restApiException = new RestApiExceptionInfo();
        ErrorCode errorCode = ErrorCode.FORBIDDEN_EXCEPTION;

        restApiException.setHttpStatus(errorCode.getHttpStatus());
        restApiException.setErrorMessage("Access Denied.");

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        String result = mapper.writeValueAsString(restApiException);
        response.getWriter().write(result);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
