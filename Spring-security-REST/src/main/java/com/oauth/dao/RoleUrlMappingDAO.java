package com.oauth.dao;

import com.oauth.data.RoleUrlMapping;

import java.util.List;

public interface RoleUrlMappingDAO {

    List<RoleUrlMapping> fetchRoleUrlMapping();
}
