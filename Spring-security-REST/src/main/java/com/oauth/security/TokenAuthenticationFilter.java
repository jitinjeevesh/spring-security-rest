package com.oauth.security;

import com.oauth.dao.AuthenticationTokenDAO;
import com.oauth.dto.AuthenticationTokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
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
        super("/"); //defaultFilterProcessesUrl - specified in applicationContext.xml.
//        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(defaultFilterProcessesUrl)); //Authentication will only be initiated for the request url matching this pattern
//        setAuthenticationManager(new NoOpAuthenticationManager());
//        setAuthenticationSuccessHandler(new TokenSimpleUrlAuthenticationSuccessHandler());
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        this.token = request.getHeader(SECURITY_TOKEN_KEY);

        System.out.println("....................Inside token validator.............................................");
        System.out.println(token);
//        if (request.getAttribute(FILTER_APPLIED) != null) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
//
//        if(request.getParameter(actionParameter) !=null &&
//                request.getParameter(actionParameter).equals("logout")) {
//            SecurityContextHolder.clearContext();
//            return;
//        }

        /*if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }*/
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


    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        RequestMatcher requestMatcher = new RequestMatcher() {
            @Override
            public boolean matches(HttpServletRequest request) {
                String url = request.getServletPath();

                if (request.getPathInfo() != null) {
                    url += request.getPathInfo();
                }
                System.out.println("..........................URL...................");
                System.out.println(url);
                return false;
            }
        };
        return requestMatcher.matches(request);
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
        AuthenticationTokenDTO authenticationTokenDTO = authenticationTokenDAO.findByAccessToken(token);
        if (authenticationTokenDTO == null) {
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
