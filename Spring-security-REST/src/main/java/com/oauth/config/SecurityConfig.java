package com.oauth.config;

import com.oauth.filters.*;
import com.oauth.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The Class SecurityConfig.
 */
@Order(Ordered.LOWEST_PRECEDENCE - 7)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private RESTAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private RESTAuthenticationFailureHandler restAuthenticationFailureHandler;
    @Autowired
    private RESTAuthenticationSuccessHandler restAuthenticationSuccessHandler;
    @Autowired
    private RESTLogoutSuccessHandler restLogoutSuccessHandler;
//    @Autowired
//    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Autowired
    @Qualifier("customUserDetailsService")
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Preauth auth provider.
     *
     * @return the pre authenticated authentication provider
     */
    @Bean
    public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
        PreAuthenticatedAuthenticationProvider preAuthenticatedProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedProvider.setPreAuthenticatedUserDetailsService(preAuthUserService());
        return preAuthenticatedProvider;
    }

    /**
     * Pre auth user service.
     *
     * @return the authentication user details service
     */
    @Bean
    public AuthenticationUserDetailsService preAuthUserService() {
        return new PreAuthenticationUserService();
    }

    /**
     * Pre auth filter.
     *
     * @return the pre authenitcation filter
     * @throws Exception the exception
     */
    @Bean
    public PreAuthenitcationFilter preAuthFilter() throws Exception {
        PreAuthenitcationFilter preAuthFilter = new PreAuthenitcationFilter();
        preAuthFilter.setAuthenticationManager(authenticationManager());
        return preAuthFilter;
    }

    //TODO:previous version
    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)
     */
/*    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(preauthAuthProvider());
    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(customUserDetailsService);
        builder.authenticationProvider(authenticationProvider());
//        builder.authenticationProvider(preauthAuthProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        authenticationProvider.setSaltSource(saltSource());
        return authenticationProvider;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.WebSecurity)
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/resources/**")
                .antMatchers("/static/**")
                .antMatchers("/fonts/**")
                .antMatchers("/css/**")
                .antMatchers("/images/**")
                .antMatchers("/app/**")
                .antMatchers("/*.html")
                .antMatchers("/*.js");
    }

    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests().antMatchers("/api/**").authenticated();

        http.addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.formLogin().loginProcessingUrl("/login").successHandler(restAuthenticationSuccessHandler).permitAll();
        http.formLogin().failureHandler(restAuthenticationFailureHandler);
        http.logout().logoutUrl("/logout").logoutSuccessHandler(restLogoutSuccessHandler);
//        logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
        // CSRF tokens handling
//        http.csrf().disable();
        http.addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);
        http.addFilterBefore(tokenAuthenticationFilter(), AnonymousAuthenticationFilter.class);
    }

    @Bean
    TokenAuthenticationFilter tokenAuthenticationFilter() {
        TokenAuthenticationFilter tokenAuthenticationFilter = new TokenAuthenticationFilter();
        tokenAuthenticationFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                System.out.println("success");
            }
        });
        tokenAuthenticationFilter.setAuthenticationManager(new NoOpAuthenticationManager());
        return tokenAuthenticationFilter;
    }


    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter()
            throws Exception {
        CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter = new CustomUsernamePasswordAuthenticationFilter();
        customUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        customUsernamePasswordAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
        customUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(restAuthenticationSuccessHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(restAuthenticationFailureHandler);
        customUsernamePasswordAuthenticationFilter.setUsernameParameter("username");
        customUsernamePasswordAuthenticationFilter.setPasswordParameter("password");
        return customUsernamePasswordAuthenticationFilter;
    }

}
