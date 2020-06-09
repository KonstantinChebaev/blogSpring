package main.web.api.user;

import main.domain.user.RegisterUserUseCase;
import main.domain.user.ResultsDto;
import main.domain.user.UserRegisterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuthController {
    @Autowired
    RegisterUserUseCase registerUserUseCase;

    @PostMapping(value = "/api/auth/register")
    public ResultsDto apiAuthRegister(@RequestBody UserRegisterDto ur) {
        return registerUserUseCase.registerUser(ur);
    }

    @PostMapping(value = "/api/auth/login")
    public ResultsDto apiAuthLogin (@RequestParam String email, @RequestParam String password) {
        return registerUserUseCase.loginUser(email, password);
    }

}
