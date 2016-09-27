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
    @Autowired
    private RESTSpringSecurityService restSpringSecurityService;

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
