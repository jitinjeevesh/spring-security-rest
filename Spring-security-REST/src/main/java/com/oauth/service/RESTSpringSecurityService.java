package com.oauth.service;

import com.oauth.authentication.config.TokenGeneratorConfig;
import com.oauth.authentication.generator.TokenGenerator;
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

    /**
     * This method is used to generate token with the pre defined strategies for token generation.
     * For eg: JWT, UUID
     *
     * @return AuthenticationToken with all the token information.
     */
    public AuthenticationToken generateToken() {
        return tokenGeneratorFactory.generate(tokenGeneratorConfig.getAuthentication());
    }

    /**
     * This method is used to generate token with the user defined strategies.
     *
     * @return AuthenticationToken with all the token information.
     */
    public AuthenticationToken generateToken(TokenGenerator tokenGenerator) {
        return tokenGenerator.generate();
    }

}
