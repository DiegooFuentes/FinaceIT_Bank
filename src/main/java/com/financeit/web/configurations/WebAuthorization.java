package com.financeit.web.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity
@Configuration
public class WebAuthorization extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()

                .antMatchers("/web/index.html").permitAll()
                .antMatchers("/web/js/index.js").permitAll()
                .antMatchers("/web/css/style.css").permitAll()
                .antMatchers("/web/css/", "/web/img/", "/web/js/").permitAll()
                .antMatchers(HttpMethod.POST,"/api/clients/register").permitAll()
                .antMatchers(HttpMethod.POST,"/api/clients/current/cards").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.GET,"/api/clients/current/accounts").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST,"/api/transactions").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST,"/api/loans").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.GET,"/api/loans").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.GET,"/api/send-notification").hasAuthority("CLIENT")
                .antMatchers("/h2-console/").permitAll()
                .antMatchers("/admin/").hasAuthority("ADMIN")
                .antMatchers("/api/**").hasAuthority("CLIENT")
                .antMatchers("/web/**").hasAuthority("CLIENT")
                .antMatchers("/rest/**").hasAuthority("ADMIN")
                .antMatchers("/").hasAuthority("CLIENT");


        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");
        http.logout().logoutUrl("/api/logout");
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null){
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

