package com.oauth.handler;

import com.oauth.config.RESTSecurityConfig;
import com.oauth.dao.LogoutDAO;
import com.oauth.service.RESTSecurityUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RESTLogoutCustomHandler implements LogoutHandler {
    @Autowired
    private LogoutDAO logoutDAO;
    @Autowired
    private RESTSecurityConfig restSecurityConfig;

    //TODO:Needs to refactor when direct call logout.
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        RESTSecurityUserDetails principal = (RESTSecurityUserDetails) authentication.getPrincipal();
        if (restSecurityConfig.isLogoutFromSession()) {
            logoutDAO.session(principal.getAccessToken().getToken());
        } else {
            logoutDAO.user(principal.getUsername());
        }
    }
}
