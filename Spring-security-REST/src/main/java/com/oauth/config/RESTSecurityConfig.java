package com.oauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RESTSecurityConfig {
    /**
     * Set this property to enable the csrf protection.
     */
    @Value("${spring.rest.csrf.enable:false}")
    private boolean isCsrfInable;

    @Value("${spring.rest.csrf.url:/csrf}")
    private String csrfUrl;

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
}
