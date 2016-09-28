package com.oauth.filters;

import com.oauth.config.RESTSecurityConfig;
import com.oauth.dao.AuthenticationTokenDAO;
import com.oauth.dao.RoleUrlMappingDAO;
import com.oauth.dao.UserDetailDAO;
import com.oauth.data.AuthenticationToken;
import com.oauth.data.RoleUrlMapping;
import com.oauth.data.User;
import com.oauth.service.RESTSecurityUserDetails;
import com.oauth.service.RESTSpringSecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final static Logger log = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Autowired
    private AuthenticationTokenDAO authenticationTokenDAO;
    @Autowired
    private UserDetailDAO userDetailDAO;
    @Autowired
    private RoleUrlMappingDAO roleUrlMappingDAO;
    @Autowired
    private RESTSecurityConfig restSecurityConfig;

    private String token = null;

    public TokenAuthenticationFilter() {
        super("/");
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws AuthenticationException, IOException, ServletException {
//        AuthenticationTrustResolverImpl
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        this.token = request.getHeader(restSecurityConfig.getTokenHeader());

        log.info("Token successfully received inside token validator", token);
//        if(request.getParameter(actionParameter) !=null &&
//                request.getParameter(actionParameter).equals("logout")) {
//            SecurityContextHolder.clearContext();
//            return;
//        }
        if (grantAuthentication(req, res)) {
            log.info("This is a permit all authentication request");
//            chain.doFilter(request, response);
        } else if (token != null) {
            Authentication authResult;
            try {
                authResult = attemptAuthentication(request, response);
                if (authResult == null) {
                    notAuthenticated(request, response, new LockedException("User Not found"));
                    return;
                }
            } catch (AuthenticationException failed) {
                notAuthenticated(request, response, failed);
                return;
            }
            try {
                successfulAuthentication(request, response, chain, authResult);
            } catch (NestedServletException e) {
                if (e.getCause() instanceof AccessDeniedException) {
                    unsuccessfulAuthentication(request, response, new LockedException("Forbidden"));
                }
            }
        } else {
            throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Bad Token"));
        }
        chain.doFilter(request, response);
    }

    private boolean grantAuthentication(ServletRequest req, ServletResponse res) {
        if (!restSecurityConfig.getExcludeAuthenticationUrls().isEmpty()) {
            for (String url : restSecurityConfig.getExcludeAuthenticationUrls()) {
                if (((HttpServletRequest) req).getRequestURI().equalsIgnoreCase(url)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Attempt to authenticate request
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String token = request.getHeader(restSecurityConfig.getTokenHeader());
        log.info("Attempt authentication for token :", token);
        if (token == null) {
            throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Bad Token"));
        }
        AbstractAuthenticationToken userAuthenticationToken = authUserByToken(response, request.getRequestURI(), token);
        if (userAuthenticationToken == null)
            throw new AuthenticationServiceException("Invalid Token");
        return userAuthenticationToken;
    }


    public void notAuthenticated(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        try {
            unsuccessfulAuthentication(request, response, new LockedException("Forbidden"));
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    /**
     * authenticate the user based on token
     *
     * @return
     */
    private AbstractAuthenticationToken authUserByToken(HttpServletResponse response,String uri, String token) throws AuthenticationException {
        if (token == null) return null;
        AuthenticationToken authenticationToken = authenticationTokenDAO.find(token);
        if (authenticationToken == null) {
            throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Bad Token"));
        }
        User user = userDetailDAO.fetchUser(authenticationToken.getUsername());
        if (!isURIAuthenticate(user, uri)) {
            throw new AccessDeniedException(MessageFormat.format("Error | {0}", "Access denied"));
        }

        RESTSecurityUserDetails restSecurityUserDetails = new RESTSecurityUserDetails(Arrays.asList(new SimpleGrantedAuthority(user.getUserRole().getRole())), user.getUserMail(), user.getPassword(), true, authenticationToken);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(restSecurityUserDetails, user.getPassword(), Arrays.asList(new SimpleGrantedAuthority(user.getUserRole().getRole())));
        log.info("Authentication successfully for token :", token);
        return usernamePasswordAuthenticationToken;
    }

    private boolean isURIAuthenticate(User user, String uri) {
        List<RoleUrlMapping> roleUrlMappings = roleUrlMappingDAO.fetchRoleUrlMapping();
        try {
            if (!roleUrlMappings.isEmpty()) {
                for (RoleUrlMapping roleUrlMapping : roleUrlMappings) {
                    System.out.println(roleUrlMapping);
                    if (roleUrlMapping.getRole().equalsIgnoreCase(user.getUserRole().getRole())) {
                        return roleUrlMapping.getUrls().contains(uri);
                    }
                }
            }
        } catch (Exception e) {
            throw new AccessDeniedException(MessageFormat.format("Error | {0}", "Access denied"));
        }
        return false;
    }
}
