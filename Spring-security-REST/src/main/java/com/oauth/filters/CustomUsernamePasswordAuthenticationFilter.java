package com.oauth.filters;

import com.google.gson.Gson;
import com.oauth.constants.SecurityConstants;
import com.oauth.data.LoginRequest;
import com.oauth.utils.MatchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final static Logger log = LoggerFactory.getLogger(CsrfTokenResponseHeaderBindingFilter.class);

    private String jsonUsername;
    private String jsonPassword;

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String password = null;
        if (MatchUtil.checkContentType(request.getHeader(SecurityConstants.CONTENT_TYPE))) {
            password = this.jsonPassword;
        } else {
            password = super.obtainPassword(request);
        }

        return password;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String username = null;
        if (MatchUtil.checkContentType(request.getHeader(SecurityConstants.CONTENT_TYPE))) {
            username = this.jsonUsername;
        } else {
            username = super.obtainUsername(request);
        }

        return username;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("Attempt authentication with JSON request, Inside CustomUsernamePasswordAuthenticationFilter");
        if (MatchUtil.checkContentType(request.getHeader(SecurityConstants.CONTENT_TYPE))) {
            try {
                StringBuffer sb = new StringBuffer();
                String line = null;
                log.info("Authentication request successfully accept for : ", sb.toString());

                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                Gson gson = new Gson();
                LoginRequest loginRequest = gson.fromJson(sb.toString(), LoginRequest.class);
                log.info("Authentication request successfully bind for : ", loginRequest.getUsername());
                this.jsonUsername = loginRequest.getUsername();
                this.jsonPassword = loginRequest.getPassword();
            } catch (Exception e) {
                log.error("Exception occur while attempt authentication", e);
            }
        }

        if (!request.getMethod().equals(SecurityConstants.POST_REQUEST)) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        username = username.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
