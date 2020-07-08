package main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //https://stackoverflow.com/questions/51026694/spring-security-blocks-post-requests-despite-securityconfig отсюда метод
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/post/moderation/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/post/my/**").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.POST, "/api/post/**").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.POST, "/api/image/**").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/post/{ID}").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.POST, "/api/moderation/**").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.POST, "/api/profile/my/**").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.GET, "/api/statistics/my/**").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.POST, "/api/post/like/**").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.POST, "/api/post/dislike/**").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.GET, "/api/auth/logout/**").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/settings/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/settings/**").hasRole("ADMIN")
                .anyRequest().permitAll();
                // .antMatchers(HttpMethod.GET, "/api/auth/check/**").hasAnyRole("USER","ADMIN")
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
    }
    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }



}