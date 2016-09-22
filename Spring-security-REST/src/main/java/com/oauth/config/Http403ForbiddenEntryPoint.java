/**
 * 
 */
package com.oauth.config;

import com.oauth.exception.ErrorCodes;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Http403ForbiddenEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authenticationException) throws IOException, ServletException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.getOutputStream().println(
        "{\"httpCode\": "+HttpServletResponse.SC_FORBIDDEN+",\"errorCode\": "+ErrorCodes.INVALID_REQUEST_DATA+",\"message\": \"Invalid Request\"}");

  }
}
