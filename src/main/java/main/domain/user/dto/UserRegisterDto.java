package main.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(value = "e_mail")
    private String email;
    private String name;
    private String password;
    private String captcha;

    @JsonProperty(value = "captcha_secret")
    private String captchaSecret;

}
