package com.oauth.dto;

import java.io.Serializable;

public class UserProfileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String userMail;

    private String firstName;

    private String lastName;

    private String password;

    private UserRoleDTO userRole;

    public UserProfileDTO() {
    }

    public UserProfileDTO(Integer id, String userMail, String firstName, String lastName, String password, UserRoleDTO userRole) {
        this.id = id;
        this.userMail = userMail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userRole = userRole;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRoleDTO getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRoleDTO userRole) {
        this.userRole = userRole;
    }

}
