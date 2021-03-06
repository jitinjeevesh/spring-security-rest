package com.oauth.dto;

import java.io.Serializable;

public class UserRoleDTO implements Serializable {

    private Integer roleId;
    private String role;

    public UserRoleDTO() {
    }

    public UserRoleDTO(Integer roleId, String role) {
        this.roleId = roleId;
        this.role = role;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
