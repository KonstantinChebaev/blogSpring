package main.web.api.user;

import main.domain.user.UserLoginDto;
import main.domain.user.UserServiceImpl;
import main.domain.user.UserAuthResponceDto;
import main.domain.user.UserRegisterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping(value = "/api/auth/")
public class ApiAuthController {
    @Autowired
    UserServiceImpl registerUserUseCase;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping(value = "register")
    public UserAuthResponceDto apiAuthRegister(@RequestBody UserRegisterDto ur) {
        return registerUserUseCase.registerUser(ur);
    }

    @PostMapping(value = "login")
    public UserAuthResponceDto apiAuthLogin (@RequestBody UserLoginDto ul) {
        try {

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(ul.getEmail(), ul.getPassword()));

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return registerUserUseCase.loginUser(ul.getEmail(), ul.getPassword());
    }

}
