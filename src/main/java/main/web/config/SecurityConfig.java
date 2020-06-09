package main.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.savedrequest.NullRequestCache;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    private BCryptPasswordEncoder passwordEncoder() {
        return SecurityUtility.passwordEncoder();
    }

    //https://stackoverflow.com/questions/51026694/spring-security-blocks-post-requests-despite-securityconfig отсюда метод
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/post/moderation/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/post/my/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/post/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/image/**").hasRole("USER")
                .antMatchers(HttpMethod.PUT, "/api/post/{ID}").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/moderation/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/profile/my/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/statistics/my/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/post/like/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/post/dislike/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/auth/logout/**").hasRole("USER")
                .antMatchers(HttpMethod.PUT, "/api/settings/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/auth/check/**").hasRole("USER")
                .anyRequest().permitAll()
//                .antMatchers(HttpMethod.GET, "/api/init/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/post/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/post/search/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/post/{ID}").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/post/byDate/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/post/byTag/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/tag/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/auth/login/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/auth/restore/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/auth/password/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/auth/register/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/statistics/all/**").permitAll()
                .and()
                .requestCache().requestCache(new NullRequestCache())
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }



}