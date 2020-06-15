package main.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class UserServiceImpl {
    @Autowired
    UserRepositoryPort userRepositoryPort;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public UserAuthResponceDto registerUser(UserRegisterDto urd){
        //проверки всякие разные
        User newUser = User.builder()
                .name(urd.getName())
                .email(urd.getE_mail())
                .code(urd.getCaptcha())
                .password(urd.getPassword())
                .isModerator(false)
                .regTime(new Date(System.currentTimeMillis()))
                .build();
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepositoryPort.save(newUser);
        return new UserAuthResponceDto(true, null);

    }

    public UserAuthResponceDto loginUser(String email, String password) {
        Optional<User> userOptional = userRepositoryPort.findByEmail(email);
        if(userOptional.isEmpty()){
            System.out.println(email);
            return new UserAuthResponceDto(false,null);
        }
        User user = userOptional.get();
        if(!passwordEncoder.matches(password, user.getPassword())){
            System.out.println(password);
            return new UserAuthResponceDto(false,null);
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        if (auth != null) {
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        System.out.println(SecurityContextHolder.getContext());
        return new UserAuthResponceDto(true,user);
    }
}
