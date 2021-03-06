package com.oauth.dto;

import java.io.Serializable;
import java.util.Date;

public class AuthenticationTokenDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer token_id;
    private String token;
    private Date expiryDateTime;
    private Integer userId;

    public AuthenticationTokenDTO() {
    }

    public AuthenticationTokenDTO(Integer token_id, String token, Date expiryDateTime, Integer userId) {
        this.token_id = token_id;
        this.token = token;
        this.expiryDateTime = expiryDateTime;
        this.userId = userId;
    }

    public Integer getToken_id() {
        return token_id;
    }

    public void setToken_id(Integer token_id) {
        this.token_id = token_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(Date expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AuthenticationTokenDTO{" +
                "token='" + token + '\'' +
                ", userId=" + userId +
                '}';
    }
}
