package com.oauth.config;

import org.neo4j.cypher.internal.compiler.v2_1.functions.Str;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private List<? extends GrantedAuthority> authorities;
    private String password;
    private String username;
    private Boolean isEnabled;
    private String accessToken;

    public CustomUserDetails(List<? extends GrantedAuthority> authorities, String username, String password, Boolean isEnabled, String accessToken) {
        this.authorities = authorities;
        this.password = password;
        this.username = username;
        this.isEnabled = isEnabled;
        this.accessToken = accessToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
