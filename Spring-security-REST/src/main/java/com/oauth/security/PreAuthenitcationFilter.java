package com.oauth.security;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * Spring security pre-authentication filter implementation
 * 
 * @author Hitesh
 */

public class PreAuthenitcationFilter extends
		AbstractPreAuthenticatedProcessingFilter {

	// HTTP Authorization header values for different security technologies
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String AUTH_TYPE_BASIC = "Basic ";
	public static final String AUTH_TYPE_COOKIE = "Cookie ";
	public static final String AUTH_TYPE_OAUTH = "OAuth";

	// HTTP request parameter values for different security technologies
	public static final String AUTH_PARAM_BASIC = "jBasic";
	public static final String AUTH_PARAM_COOKIE = "jCookie";
	public static final String AUTH_PARAM_OAUTH = "access_token";

	@Override
	protected Object getPreAuthenticatedPrincipal(
			HttpServletRequest httpServletRequest) {

		/*
		 * Find the access_token by looking through the http servlet request 1)
		 * Look for an access_token request parameter 2) Look in the
		 * Authorization header 3) Look for an access_token cookie
		 */

		return getAuthenticationValue(httpServletRequest);
	}

	protected String getAuthenticationValue(HttpServletRequest request) {
		String value;

		// Look for parameter first
		final String paramName = AUTH_PARAM_OAUTH;
		value = request.getParameter(paramName);

		if (null == value) {

			// Look for header next
			String authorization = request.getHeader(HEADER_AUTHORIZATION);
			if (null != authorization
					&& authorization.startsWith(AUTH_TYPE_OAUTH)) {

				// strip auth mechanism from header value
				value = authorization.substring(AUTH_TYPE_OAUTH.length());
			}

		}

		return value;
	}

	@Override
	protected Object getPreAuthenticatedCredentials(
			HttpServletRequest httpServletRequest) {
		// There are no pre authentication credentials
		// Must return a value or the authentication manager will always reject
		// the user, ever for anonymous methods
		return "dummyCredentials";
	}

}
