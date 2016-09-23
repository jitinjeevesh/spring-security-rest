package com.oauth.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenGeneratorConfig {

    /**
     * Authentication name will be the token generation mechanism name
     * i.e. JWT or UUID etc.
     */
    @Value("${spring.rest.authentication.name}")
    private String authentication;

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }
}
