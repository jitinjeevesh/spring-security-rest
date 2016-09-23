package com.oauth.service;

import com.oauth.authentication.config.TokenGeneratorConfig;
import com.oauth.authentication.generator.TokenGeneratorFactory;
import com.oauth.data.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RESTSpringSecurityService {

    @Autowired
    private TokenGeneratorConfig tokenGeneratorConfig;
    @Autowired
    private TokenGeneratorFactory tokenGeneratorFactory;

    public AuthenticationToken generateToken() {
        return tokenGeneratorFactory.generate(tokenGeneratorConfig.getAuthentication());
    }

}
