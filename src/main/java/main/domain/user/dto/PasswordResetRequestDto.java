package main.domain.user.dto;

import lombok.Data;

@Data
public class PasswordResetRequestDto {
    private String code;
    private String password;
    private String captcha;
    private String captchaSecret;
}
