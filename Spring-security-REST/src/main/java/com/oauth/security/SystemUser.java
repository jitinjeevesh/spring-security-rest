package com.oauth.security;

import com.oauth.dto.UserProfileDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SystemUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    private UserProfileDTO userProfile;
    private boolean hasExpired;

    public SystemUser() {
    }

    public SystemUser(UserProfileDTO userProfile, boolean hasExpired) {
        this.userProfile = userProfile;
        this.hasExpired = hasExpired;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {

	/*	Collection<GrantedAuthority> grantedAuthorities = new ArrayList();

		grantedAuthorities.add(new GrantedAuthority() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getAuthority() {
				return userProfile.getUserRole().getUserRole();
			}
		});

		return grantedAuthorities;*/
        return null;
    }

    @Override
    public String getPassword() {
        return "dummyPassword";
    }

    @Override
    public String getUsername() {
        if (null != userProfile.getUserMail()) {
            return userProfile.getUserMail();
        } else {
            return String.format("Anonymous[%d]", userProfile.getId());
        }
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
        return !hasExpired;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (null == otherObject) {
            return false;
        }

        if (!otherObject.getClass().isAssignableFrom(UserProfileDTO.class)) {
            return false;
        }

        return getUsername().equals(((UserProfileDTO) otherObject).getUserMail());
    }

    @Override
    public int hashCode() {
        return userProfile.getId().hashCode();
    }

    public UserProfileDTO getUser() {
        return userProfile;
    }

}
