package com.oauth.authentication.generator;

import com.oauth.data.AuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("uuidTokenGenerator")
public class UUIDTokenGenerator implements TokenGenerator {

    @Override
    public AuthenticationToken generate() {
        String token = UUID.randomUUID().toString();

        return null;
    }
}