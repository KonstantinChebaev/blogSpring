package main.config;

import main.dao.UserRepository;
import main.domain.user.User;
import main.domain.user.UserServise;
import main.security.CustomAuthenticationHandler;
import main.security.JsonUsernamePasswordAuthenticationFilter;
import main.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Optional;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserServise userServise;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var jsonResponseHandler = customAuthenticationFailureHandler();
        http
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
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
                .antMatchers("/**").permitAll()
                .and()
                .addFilterBefore(authenticationFilter(jsonResponseHandler), UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(jsonResponseHandler)
                .and()
                .logout()
                .permitAll();
                // .antMatchers(HttpMethod.GET, "/api/auth/check/**").hasAnyRole("USER","ADMIN")
  //               .antMatchers(HttpMethod.GET, "/api/settings/**").hasRole("ADMIN")
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

    @Bean
    public CustomAuthenticationHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationHandler(userServise);
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter authenticationFilter(CustomAuthenticationHandler authHandler) throws Exception {
        var authenticationFilter = new JsonUsernamePasswordAuthenticationFilter();

        authenticationFilter.setAuthenticationSuccessHandler(authHandler);
        authenticationFilter.setAuthenticationFailureHandler(authHandler);
        authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/auth/login", "POST"));
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationFilter;
    }

    public class UserDetailsServiceImpl implements UserDetailsService {
        @Autowired
        UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if(userOptional.isEmpty()) {
                System.out.println("Data recieved. User not found.");
                throw new UsernameNotFoundException("Username with email"+email+" not found");
            }
            return new UserDetailsImpl(userOptional.get());
        }
    }


}