package sample.service;

import sample.model.UserProfile;
import sample.model.UserSession;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


// TODO: Auto-generated Javadoc

/**
 * The Interface UserProfileService.
 */
public interface UserProfileService {

    /**
     * Do login.
     *
     * @param users    the users
     * @param deviceId the device id
     * @return the response entity
     */
    public UserSession doLogin(UserProfile users, String deviceId);

    /**
     * Gets the users object.
     *
     * @param request the request
     * @return the users object
     */
    public UserProfile getUsersObject(HttpServletRequest request);

    /**
     * Validate secret key.
     *
     * @param string the string
     * @return the string
     */
    public String validateSecretKey(String string);

    UserProfile fetchUser(String username);

    Map register(String username, String password);

}
