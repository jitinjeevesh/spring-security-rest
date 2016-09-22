package com.oauth.dao;

import com.oauth.dto.AuthenticationTokenDTO;
import com.oauth.dto.UserProfileDTO;

public interface AuthenticationTokenDAO {

    AuthenticationTokenDTO findByAccessToken(String token);

    String generate(UserProfileDTO userProfileDTO);
}
