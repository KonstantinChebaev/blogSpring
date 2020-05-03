package main.domain.user;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {
    @Email
    @Name("e_mail")
    private String email;
    private String name;
    private String password;
    private String captcha;
    @Name("captcha_secret")
    private String captchaSecret;

}
