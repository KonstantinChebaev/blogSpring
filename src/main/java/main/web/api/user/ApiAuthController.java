package main.web.api.user;

import main.domain.user.RegisterUserUseCase;
import main.domain.user.ResultsDto;
import main.domain.user.UserRegisterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuthController {
    @Autowired
    RegisterUserUseCase registerUserUseCase;

    @PostMapping(value = "/api/auth/register")
    public ResultsDto apiAuthRegister(@RequestParam String e_mail ,
                                  @RequestParam String name,
                                  @RequestParam String password,
                                  @RequestParam String captcha,
                                  @RequestParam String captcha_secret) {
        UserRegisterDto ur = new UserRegisterDto(e_mail,name,password,captcha,captcha_secret);
        return registerUserUseCase.registerUser(ur);
    }
    @PostMapping(value = "/api/auth/login")
    public String apiAuthLogin () {

        return "  ";
    }

}
