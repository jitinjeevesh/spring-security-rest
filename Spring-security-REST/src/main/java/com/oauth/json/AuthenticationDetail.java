package com.oauth.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationDetail {

    @JsonProperty("username")
    private String username;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;


    public AuthenticationDetail() {
    }

    public AuthenticationDetail(String username, String accessToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.username = username;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

}
