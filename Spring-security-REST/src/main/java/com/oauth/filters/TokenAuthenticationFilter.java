package com.oauth.filters;

import com.oauth.dao.AuthenticationTokenDAO;
import com.oauth.data.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private AuthenticationTokenDAO authenticationTokenDAO;

    public final String SECURITY_TOKEN_KEY = "X-Auth-Token";
    private String token = null;

    public TokenAuthenticationFilter() {
        super("/");
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        this.token = request.getHeader(SECURITY_TOKEN_KEY);

        System.out.println("....................Inside token validator.............................................");
        System.out.println(token);
//        if(request.getParameter(actionParameter) !=null &&
//                request.getParameter(actionParameter).equals("logout")) {
//            SecurityContextHolder.clearContext();
//            return;
//        }
        if (token != null) {
            Authentication authResult;
            try {
                System.out.println(".............Attempt authentication...................");
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


    /**
     * Attempt to authenticate request
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException,
            IOException, ServletException {
        String token = request.getHeader(SECURITY_TOKEN_KEY);
        logger.info("token found:" + token);
        System.out.println("....................Token found.........................");
        System.out.println(token);

        //TODO:Change this to custom exception.
        if (token == null) {
            throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Bad Token"));
        }

        AbstractAuthenticationToken userAuthenticationToken = authUserByToken(token);
        System.out.println("...................user auth token.............");
        System.out.println(userAuthenticationToken);
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
    private AbstractAuthenticationToken authUserByToken(String token) {
        if (token == null) return null;

        //  String username = getUserNameFromToken(); //logic to extract username from token
        //String role = getRolesFromToken(); //extract role information from token

//        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//        authorities.add(new SimpleGrantedAuthority(role));
        AuthenticationToken authenticationToken = authenticationTokenDAO.find(token);
        if (authenticationToken == null) {
            throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Bad Token"));
        }
        User principal = new User("jpandey@dminc.com", "password", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        System.out.println("..........................user................................");
        System.out.println(principal);
        System.out.println(token);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken("jpandey@dminc.com", "password", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        System.out.println(usernamePasswordAuthenticationToken);
        return usernamePasswordAuthenticationToken;
    }
}
