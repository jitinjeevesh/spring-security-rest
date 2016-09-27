package com.oauth.service;

import com.oauth.authentication.config.TokenGeneratorConfig;
import com.oauth.authentication.generator.TokenGenerator;
import com.oauth.authentication.generator.TokenGeneratorFactory;
import com.oauth.crypto.CryptoConfig;
import com.oauth.crypto.PasswordGeneratorFactory;
import com.oauth.data.AuthenticationToken;
import com.oauth.data.RoleUrlMapping;
import org.neo4j.cypher.internal.compiler.v2_1.functions.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RESTSpringSecurityService {

    @Autowired
    private TokenGeneratorConfig tokenGeneratorConfig;
    @Autowired
    private TokenGeneratorFactory tokenGeneratorFactory;
    @Autowired
    private PasswordGeneratorFactory passwordGeneratorFactory;
    @Autowired
    private CryptoConfig cryptoConfig;

    /**
     * This method is used to save token with the pre defined strategies for token generation.
     * For eg: JWT, UUID
     *
     * @return AuthenticationToken with all the token information.
     */
    public AuthenticationToken generateToken() {
        return tokenGeneratorFactory.generate(tokenGeneratorConfig.getAuthentication());
    }

    /**
     * This method is used to save token with the user defined strategies.
     *
     * @return AuthenticationToken with all the token information.
     */
    public AuthenticationToken generateToken(TokenGenerator tokenGenerator) {
        return tokenGenerator.generate();
    }

    public String encodePassword(String password) {
        PasswordEncoder passwordEncoder = passwordGeneratorFactory.apply(cryptoConfig.getAlgorithm());
        return passwordEncoder.encode(password);
    }
}