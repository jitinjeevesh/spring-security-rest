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

    public boolean isCsrfInable() {
        return isCsrfInable;
    }

    public void setIsCsrfInable(boolean isCsrfInable) {
        this.isCsrfInable = isCsrfInable;
    }
}
