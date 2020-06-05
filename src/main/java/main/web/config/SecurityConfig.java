package main.web.config;


import main.web.security.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.core.env.Environment;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    private static final String[] PUBLIC_MATCHERS = {
            "/api/init/**",
            "/api/calendar/**",
            "/**"
    };
    @Autowired
    private Environment env;

    @Autowired
    private UserSecurityService userSecurityService;

    private BCryptPasswordEncoder passwordEncoder() {
        return SecurityUtility.passwordEncoder();
    }

    //https://stackoverflow.com/questions/51026694/spring-security-blocks-post-requests-despite-securityconfig отсюда метод
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable().httpBasic().and().
                authorizeRequests()
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
                .and().requestCache().requestCache(new NullRequestCache())
                .and().authorizeRequests().antMatchers(PUBLIC_MATCHERS).permitAll().anyRequest().authenticated();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userSecurityService).passwordEncoder(passwordEncoder());
    }

}