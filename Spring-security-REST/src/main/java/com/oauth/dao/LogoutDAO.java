package com.oauth.dao;

/**
 * This interface is uses to logout user.
 */
public interface LogoutDAO {

    /**
     * Logout particular session/
     *
     * @param token the users
     */
    public void session(String token);


    /**
     * Logout all sessions.
     *
     * @param username the users
     */
    public void user(String username);
}
