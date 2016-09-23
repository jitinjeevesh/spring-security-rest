package com.oauth.authentication.generator;

import com.oauth.data.AuthenticationToken;

public interface TokenGenerator {

    AuthenticationToken generate();
}
