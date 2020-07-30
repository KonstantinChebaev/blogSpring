package main.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.domain.user.UserServise;
import main.domain.user.dto.LoggedInUserDto;
import main.domain.user.dto.UserAuthResponceDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationHandler implements
        AuthenticationFailureHandler,
        AuthenticationSuccessHandler,
        LogoutSuccessHandler {


    private ObjectMapper objectMapper = new ObjectMapper();
    private UserServise userServise;

    public CustomAuthenticationHandler(UserServise userServise) {
        this.userServise = userServise;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException e) throws IOException, ServletException {
        objectMapper.writeValue(response.getOutputStream(), false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl userDetails = UserDetailsImpl.fromAuth(authentication);

        LoggedInUserDto loggedInUser = userServise.getLoggedInUser(userDetails.getId());

        objectMapper.writeValue(response.getOutputStream(), new UserAuthResponceDto(true,loggedInUser));
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        objectMapper.writeValue(response.getOutputStream(), true);
    }
}
