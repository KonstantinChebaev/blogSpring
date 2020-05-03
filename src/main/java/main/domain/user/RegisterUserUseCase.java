package main.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class RegisterUserUseCase {
    @Autowired
    UserRepositoryPort userRepositoryPort;

    public ResultsDto registerUser(UserRegisterDto urd){
        //проверки всякие разные
        User newUser = User.builder()
                .name(urd.getName())
                .email(urd.getEmail())
                .code(urd.getCaptcha())
                .password(urd.getPassword())
                .isModerator(false)
                .regTime(new Date(System.currentTimeMillis()))
                .build();
        userRepositoryPort.save(newUser);
        return new ResultsDto(true);

    }
}
