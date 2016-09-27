package com.oauth.service;

import com.oauth.dao.AuthenticationTokenDAO;
import com.oauth.dao.UserDetailDAO;
import com.oauth.data.AuthenticationToken;
import com.oauth.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * <p/>
 * Custom REST implementation of {@code UserDetailsService} which find user from the implemented method {@link UserDetailDAO#fetchUser(String)}.
 * <p/>
 * Mainly used to find the user from username, generate token from {@link RESTSpringSecurityService#generateToken()}.
 * And persist the token by implementing {@link AuthenticationTokenDAO#save(String, AuthenticationToken)}
 *
 * @author Jeevesh Pandey
 */
@Component("restSecurityUserDetailsService")
public class RESTSecurityUserDetailsService implements UserDetailsService {

    private final static Logger log = LoggerFactory.getLogger(RESTSecurityUserDetailsService.class);
    @Autowired
    private UserDetailDAO userDetailDAO;
    @Autowired
    private AuthenticationTokenDAO authenticationTokenDAO;
    @Autowired
    private RESTSpringSecurityService restSpringSecurityService;

    /**
     * Locates the user based on the username. In this case, the
     * <code>RESTSecurityUserDetails</code> object that comes back may have a username that is of a different case than what was
     * actually requested. User searching is dependent upon the implementation of ${@link UserDetailDAO#fetchUser(String)}.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(" Received user name", username);
        User userProfile = userDetailDAO.fetchUser(username);
        if (userProfile != null) {
            AuthenticationToken authenticationToken = restSpringSecurityService.generateToken();
            authenticationTokenDAO.save(username, authenticationToken);
            return new RESTSecurityUserDetails(Arrays.asList(new SimpleGrantedAuthority(userProfile.getUserRole().getRole())), userProfile.getUserMail(), userProfile.getPassword(), true, authenticationToken);
        } else {
            throw new UsernameNotFoundException("User name not found");
        }
    }
}
