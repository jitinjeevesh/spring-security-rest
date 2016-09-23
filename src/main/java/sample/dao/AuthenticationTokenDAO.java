package sample.dao;

import com.oauth.data.AuthenticationToken;

public interface AuthenticationTokenDAO {

    AuthenticationToken findByAccessToken(String token);

    String generate(String username);
}
