package com.oauth.security;

import com.oauth.dao.AuthenticationTokenDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class PreAuthenticationUserService implements AuthenticationUserDetailsService {

  private static final Logger log = Logger.getLogger(PreAuthenticationUserService.class.getName());

  @Autowired
  private AuthenticationTokenDAO authenticationTokenDao;

  @Override
  @Transactional
  public UserDetails loadUserDetails(Authentication authentication){

    /*
     * Find a connection based on the access_token. Check the expiration date. Find the user
     * associated with the connection
     */

 /*   boolean expired = false;
    String accessToken = (String) authentication.getPrincipal();
    if (null != accessToken) {

      log.info("Found access_token : " + accessToken);
      AuthenticationTokenDTO userSession = authenticationTokenDao.findByAccessToken(accessToken.trim());
      if (null != userSession) {

        log.info("userSession Found");

        if (userSession.getExpiryDateTime().before(new Date())) {
          expired = true;
        }
        UserProfile userDomain = userSession.getUserId();
        if (userDomain != null) {
          log.info("role of the user: " + userDomain.getUserRole().getUserRole());
          return new SystemUser(userDomain, expired);
        }

      }

    }

    log.info("No user found for authentication token " + authentication);

    throw new UsernameNotFoundException(String.format("No user found for authentication token %s",
        authentication));*/
    return null;
  }

}
