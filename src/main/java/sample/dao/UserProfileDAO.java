package sample.dao;

import com.oauth.data.AuthenticationToken;
import com.oauth.data.User;

/**
 * The Interface UserDetailDAO.
 */
public interface UserProfileDAO {

    /**
     * Gets the login.
     *
     * @param users the users
     * @return the login
     */
    public String getLogin(User users);

    /**
     * Update user session.
     *
     * @param session the session
     * @return the user session
     */
    public AuthenticationToken updateUserSession(AuthenticationToken session);

    /**
     * Fetch user.
     *
     * @param username the users
     * @return the users
     */
    public User fetchUser(String username);

    /**
     * Validate token.
     *
     * @param header the header
     * @return the user session
     */
    public AuthenticationToken validateToken(String header);

    /**
     * Fetch user by user id.
     *
     * @param id the id
     * @return the users
     */
    public User fetchUserByUserId(Integer id);


}
