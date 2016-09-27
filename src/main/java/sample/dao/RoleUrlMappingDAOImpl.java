package sample.dao;

import com.oauth.dao.RoleUrlMappingDAO;
import com.oauth.data.RoleUrlMapping;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleUrlMappingDAOImpl implements RoleUrlMappingDAO {

    @Override
    public List<RoleUrlMapping> fetchRoleUrlMapping() {
        List<RoleUrlMapping> roleUrlMappings = new ArrayList<RoleUrlMapping>();

        RoleUrlMapping userRoleUrlMapping = new RoleUrlMapping();
        userRoleUrlMapping.setRole("ROLE_USER");
        userRoleUrlMapping.setUrls(new ArrayList<String>() {{
            add("/api/user");
            add("/api/open");
        }});

        RoleUrlMapping adminRoleUrlMapping = new RoleUrlMapping();
        adminRoleUrlMapping.setRole("ROLE_ADMIN");
        adminRoleUrlMapping.setUrls(new ArrayList<String>() {{
            add("/api/admin");
            add("/api/open");
        }});

        RoleUrlMapping isAuthAnonymously = new RoleUrlMapping();
        isAuthAnonymously.setRole("IS_AUTHENTICATED_ANONYMOUSLY");
        isAuthAnonymously.setUrls(new ArrayList<String>() {{
            add("/api/admin");
            add("/api/open");
        }});

        RoleUrlMapping isAuthFully = new RoleUrlMapping();
        isAuthFully.setRole("IS_FULLY_AUTHENTICATED");
        isAuthFully.setUrls(new ArrayList<String>() {{
            add("/api/all");
        }});

        roleUrlMappings.add(userRoleUrlMapping);
        roleUrlMappings.add(adminRoleUrlMapping);
        roleUrlMappings.add(isAuthFully);
        return roleUrlMappings;
    }
}
