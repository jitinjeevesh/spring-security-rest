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

@Component("restSecurityUserDetailsService")
public class RESTSecurityUserDetailsService implements UserDetailsService {

    private final static Logger log = LoggerFactory.getLogger(RESTSecurityUserDetailsService.class);
    @Autowired
    private UserDetailDAO userDetailDAO;
    @Autowired
    private AuthenticationTokenDAO authenticationTokenDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = new User();
        user.setUserMail(username);
        log.info(" Received user name", username);
        User userProfile = userDetailDAO.fetchUser(username);
        User userProfileDTO = new User();
        userProfileDTO.setUserMail(username);
        AuthenticationToken accessToken = authenticationTokenDAO.generate(username);
        if (userProfile != null) {
            return new RESTSecurityUserDetails(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")), userProfile.getUserMail(), userProfile.getPassword(), true, accessToken);
        } else {
            throw new UsernameNotFoundException("User name not found");
        }
    }
}
