package com.oauth.config;

import com.oauth.dao.AuthenticationTokenDAO;
import com.oauth.dao.UserProfileDAO;
import com.oauth.dto.UserProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserProfileDAO userProfileDAO;
    @Autowired
    private AuthenticationTokenDAO authenticationTokenDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserProfileDTO user = new UserProfileDTO();
        user.setUserMail(username);
        System.out.printf("....user name.........");
        System.out.printf(username);
        UserProfileDTO userProfile = userProfileDAO.fetchUser(user);
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUserMail("jpandey@dminc.com");
        String accessToken = authenticationTokenDAO.generate(userProfileDTO);

        System.out.println("..................Data access...........");
        System.out.println(userProfile);
        if (userProfile != null) {
            return new CustomUserDetails(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")), userProfile.getUserMail(), userProfile.getPassword(), true, accessToken);
        } else {
            throw new UsernameNotFoundException("User name not found");
        }
    }
}
