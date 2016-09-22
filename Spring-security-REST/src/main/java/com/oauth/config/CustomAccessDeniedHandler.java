package com.oauth.config;

import com.oauth.exception.ErrorCodes;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler{

	/* (non-Javadoc)
	 * @see org.springframework.security.web.access.AccessDeniedHandler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.access.AccessDeniedException)
	 */
	@Override
	public void handle(HttpServletRequest request,HttpServletResponse response,AccessDeniedException accessDeniedException) throws IOException,ServletException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	    response.getOutputStream().println("{\"httpCode\": "+HttpServletResponse.SC_FORBIDDEN+",\"errorCode\": "+ErrorCodes.INVALID_REQUEST_DATA+",\"message\": \"Invalid Request\"}");
		
	}

}
