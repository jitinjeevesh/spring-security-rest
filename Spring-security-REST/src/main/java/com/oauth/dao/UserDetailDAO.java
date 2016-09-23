package com.oauth.dao;

import com.oauth.data.User;

/**
 * This interface is uses for retrieving the user information.
 */
public interface UserDetailDAO {

    /**
     * Fetch user.
     *
     * @param username the users
     * @return the users
     */
    public User fetchUser(String username);
}
