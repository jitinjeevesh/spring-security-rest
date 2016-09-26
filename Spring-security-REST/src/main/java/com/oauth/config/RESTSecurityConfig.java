package com.oauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RESTSecurityConfig {
    /**
     * Set this property to enable the csrf protection.
     */
    @Value("${spring.security.rest.csrf.enable:false}")
    private boolean isCsrfInable;

    @Value("${spring.security.rest.csrf.url:/csrf}")
    private String csrfUrl;

    @Value("${spring.security.rest.username.key:username}")
    private String username;

    @Value("${spring.security.rest.password.key:password}")
    private String password;

    @Value("${spring.security.rest.fully.authenticated.role:PERMIT_ALL}")
    private String permitAll;

    public boolean isCsrfInable() {
        return isCsrfInable;
    }

    public void setIsCsrfInable(boolean isCsrfInable) {
        this.isCsrfInable = isCsrfInable;
    }

    public String getCsrfUrl() {
        return csrfUrl;
    }

    public void setCsrfUrl(String csrfUrl) {
        this.csrfUrl = csrfUrl;
    }

    public String getPermitAll() {
        return permitAll;
    }

    public void setPermitAll(String permitAll) {
        this.permitAll = permitAll;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
