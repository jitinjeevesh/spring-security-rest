package com.oauth.dao;

import com.oauth.data.AuthenticationToken;

/**
 * This interface is uses for authentication token implementation.
 */
public interface AuthenticationTokenDAO {

    /**
     * This method is used to validate the authentication token.
     *
     * @param token String token
     * @return AuthenticationToken with token information.
     */
    AuthenticationToken find(String token);


    /**
     * This method is used to generate the authentication token for particular user.
     *
     * @param username String token
     * @return AuthenticationToken with token information.
     */
    AuthenticationToken generate(String username);
}
