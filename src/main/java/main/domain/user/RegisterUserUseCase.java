package main.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class RegisterUserUseCase {
    @Autowired
    UserRepositoryPort userRepositoryPort;

    public ResultsDto registerUser(UserRegisterDto urd){
        //проверки всякие разные
        User newUser = User.builder()
                .name(urd.getName())
                .email(urd.getE_mail())
                .code(urd.getCaptcha())
                .password(urd.getPassword())
                .isModerator(false)
                .regTime(new Date(System.currentTimeMillis()))
                .build();
        userRepositoryPort.save(newUser);
        return new ResultsDto(true, null);

    }

    public ResultsDto loginUser(String email, String password) {
        Optional<User> userOptional = userRepositoryPort.findByEmail(email);
        if(userOptional.isEmpty()){
            return new ResultsDto(false,null);
        }
        User user = userOptional.get();
        if(!user.getPassword().equals(password)){
            return new ResultsDto(false,null);
        }
        return new ResultsDto(true,user);
    }
}
