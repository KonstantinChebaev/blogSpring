package main.domain.user;

import main.domain.user.dto.UserAuthResponceDto;
import main.domain.user.dto.UserRegisterDto;
import main.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class UserAuthUseCase {
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
        User user;
        try {
            user = userRepositoryPort.findByEmail(email);
        } catch (Exception e){
            return new UserAuthResponceDto(false,null);
        }
        if(!passwordEncoder.matches(password, user.getPassword())){
            System.out.println(password);
            return new UserAuthResponceDto(false,null);
        }
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(user, "", userDetails.getAuthorities());
        if (auth != null) {
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        System.out.println(SecurityContextHolder.getContext());
        return new UserAuthResponceDto(true,user);
    }
    public User getCurrentUser (HttpServletRequest request){
        if(request.isRequestedSessionIdValid()){
            String principalName = request.getUserPrincipal().getName();
            String email = principalName.substring(principalName.indexOf(" email=")+7,principalName.indexOf(", password="));
            return userRepositoryPort.findByEmail(email);
        } else {
            return null;
        }
    }
}
