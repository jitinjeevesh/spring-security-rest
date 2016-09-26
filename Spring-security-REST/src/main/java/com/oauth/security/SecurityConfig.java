package com.oauth.security;

import com.oauth.config.RESTSecurityConfig;
import com.oauth.dao.RoleUrlMappingDAO;
import com.oauth.data.RoleUrlMapping;
import com.oauth.filters.CsrfTokenResponseHeaderBindingFilter;
import com.oauth.filters.CustomUsernamePasswordAuthenticationFilter;
import com.oauth.filters.TokenAuthenticationFilter;
import com.oauth.handler.*;
import com.oauth.service.RESTSecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private RESTTokenAuthenticationSuccessHandler restTokenAuthenticationSuccessHandler;
    @Autowired
    private RESTSecurityConfig restSecurityConfig;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
        p.setIgnoreResourceNotFound(true);
        p.setIgnoreUnresolvablePlaceholders(true);
        return p;
    }

    @Autowired
    @Qualifier("restSecurityUserDetailsService")
    private RESTSecurityUserDetailsService restSecurityUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(restSecurityUserDetailsService);
        builder.authenticationProvider(authenticationProvider());
//        builder.authenticationProvider(preauthAuthProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(restSecurityUserDetailsService);
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
//        http.authorizeRequests().antMatchers("/api/all").permitAll();
        http.authorizeRequests().antMatchers("/**").authenticated();
        http.addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.formLogin().loginProcessingUrl("/login").successHandler(restAuthenticationSuccessHandler).permitAll();
        http.formLogin().failureHandler(restAuthenticationFailureHandler);
        http.logout().logoutUrl("/logout").logoutSuccessHandler(restLogoutSuccessHandler);
//        logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
        // CSRF tokens handling
        if (restSecurityConfig.isCsrfInable()) {
            CsrfTokenResponseHeaderBindingFilter csrfTokenResponseHeaderBindingFilter = new CsrfTokenResponseHeaderBindingFilter();
            csrfTokenResponseHeaderBindingFilter.setCsrfUrl(restSecurityConfig.getCsrfUrl());
            http.addFilterAfter(csrfTokenResponseHeaderBindingFilter, CsrfFilter.class);
        } else {
            http.csrf().disable();
        }
        http.addFilterBefore(tokenAuthenticationFilter(), AnonymousAuthenticationFilter.class);
    }

    @Bean
    TokenAuthenticationFilter tokenAuthenticationFilter() {
        TokenAuthenticationFilter tokenAuthenticationFilter = new TokenAuthenticationFilter();
        tokenAuthenticationFilter.setAuthenticationSuccessHandler(restTokenAuthenticationSuccessHandler);
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
