package com.oauth.dao;

import com.oauth.dto.AuthenticationTokenDTO;
import com.oauth.dto.UserProfileDTO;

/**
 * The Interface UserProfileDAO.
 */
public interface UserProfileDAO {

    /**
     * Gets the login.
     *
     * @param users the users
     * @return the login
     */
    public String getLogin(UserProfileDTO users);

    /**
     * Update user session.
     *
     * @param session the session
     * @return the user session
     */
    public AuthenticationTokenDTO updateUserSession(AuthenticationTokenDTO session);

    /**
     * Fetch user.
     *
     * @param users the users
     * @return the users
     */
    public UserProfileDTO fetchUser(UserProfileDTO users);

    /**
     * Validate token.
     *
     * @param header the header
     * @return the user session
     */
    public AuthenticationTokenDTO validateToken(String header);

    /**
     * Fetch user by user id.
     *
     * @param id the id
     * @return the users
     */
    public UserProfileDTO fetchUserByUserId(Integer id);


}
