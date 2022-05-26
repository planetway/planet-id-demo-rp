package com.planetway.fudosan.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(
                        getRestAuthenticationEntryPoint(),
                        new AntPathRequestMatcher("/api/**"))
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/property", "/actuator/**", "/login", "/redirect-to-pcore", "/callback/login", "/user/sign-up").permitAll()
                .antMatchers("/js/**", "/images/**", "/fonts2/**", "/fonts/**", "/css/**").permitAll()
                .antMatchers(HttpMethod.POST, "/user", "/api/user", "/api/authenticate", "/api/authenticate-planet-id", "/api/authenticate-planet-id/callback").permitAll()
                .antMatchers(HttpMethod.GET, "/api/health/**").permitAll()
                .anyRequest().fullyAuthenticated()
                .and().formLogin()
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/j_spring_security_check")
                .failureUrl("/login?authError")
                .and().logout().permitAll()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")

        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    private AuthenticationEntryPoint getRestAuthenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }
}
