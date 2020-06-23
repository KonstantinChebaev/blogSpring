package main.domain.user.dto;

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
    private String e_mail;
    private String name;
    private String password;
    private String captcha;
    private String captcha_secret;

}
