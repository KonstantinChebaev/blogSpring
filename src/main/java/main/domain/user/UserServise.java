package main.domain.user;

import main.security.CaptchaServise;
import main.domain.DtoConverter;
import main.domain.ResultResponse;
import main.domain.StorageService;
import main.domain.user.dto.*;
import main.security.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Component
public class UserServise {
    private static final int HASH_LENGHT = 16;

    @Autowired
    UserRepositoryPort userRepositoryPort;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    CaptchaServise captchaServise;

    @Autowired
    private DtoConverter dtoConverter;

    @Autowired
    Environment environment;

    @Autowired
    StorageService storageService;

    @Autowired
    AuthenticationManager authenticationManager;

    public ResponseEntity<?> registerUser(UserRegisterDto urd){
        HashMap <String, Object> errors = new HashMap<>();
        User userFromDB = userRepositoryPort.findUserByEmail(urd.getEmail());
        if (userFromDB != null) {
            errors.put("email", "Этот адрес уже зарегистрирован.");
        }
        if(urd.getName().length()<4){
            errors.put("name", "Имя указано неверно.");
        }
        errors.putAll(validateUserInputAndGetErrors(urd.getPassword(),urd.getCaptcha(),urd.getCaptchaSecret()));
        if(!errors.isEmpty()){
            return new ResponseEntity<>(new ResultResponse(false, errors), HttpStatus.BAD_REQUEST);
        }
        User newUser = User.builder()
                .name(urd.getName())
                .email(urd.getEmail())
                .password(passwordEncoder.encode(urd.getPassword()))
                .isModerator(false)
                .regTime(LocalDateTime.now())
                .build();
        userRepositoryPort.save(newUser);
        LoggedInUserDto loggedInUserDto = dtoConverter.userToLoggedInUser(newUser);
        return new ResponseEntity<>(new UserAuthResponceDto(true, loggedInUserDto), HttpStatus.OK);
    }

    public ResultResponse resetUserPassword(PasswordResetRequestDto request) {
        HashMap <String, Object> errors = new HashMap<>();
        User userFromDB = userRepositoryPort.findByCode(request.getCode());
        if (userFromDB == null) {
            errors.put("code", "Ссылка для восстановления пароля устарела.\n <a href=/auth/restore>Запросить ссылку снова</a>");
        }
        errors.putAll(validateUserInputAndGetErrors(request.getPassword(),request.getCaptcha(),request.getCaptchaSecret()));
        if(!errors.isEmpty()){
            return new ResultResponse(false, errors);
        }
        userFromDB.setCode(null);
        userFromDB.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepositoryPort.save(userFromDB);
        return new ResultResponse(true, null);
    }

    private HashMap<String, Object> validateUserInputAndGetErrors(String password, String captcha, String captchaSecret) {
        HashMap<String, Object> errors = new HashMap<>();
        if (password == null || password.length() < 6) {
            errors.put("password", "Пароль короче 6 символов");
        }
        if (!captchaServise.isValidCaptcha(captcha, captchaSecret)) {
            errors.put("captcha", "Код с картинки введен неверно.");
        }
        return errors;
    }

    public UserAuthResponceDto loginUser(String email, String password, HttpServletRequest request) {
        User user = userRepositoryPort.findByEmail(email);
        if (user == null){
            return new UserAuthResponceDto(false,null);
        }
        if(!passwordEncoder.matches(password, user.getPassword())){
            return new UserAuthResponceDto(false,null);
        }

        var authReq = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authenticationManager.authenticate(authReq);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        LoggedInUserDto loggedInUserDto = dtoConverter.userToLoggedInUser(user);
        return new UserAuthResponceDto(true,loggedInUserDto);
    }


    public User getCurrentUser(HttpServletRequest request){
        if(request.isRequestedSessionIdValid() && request.getUserPrincipal()!=null){
            String email = request.getUserPrincipal().getName();
            if (email != null){
                return userRepositoryPort.findByEmail(email);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Boolean restoreUserPassword(String email) {
        User userFromDB = userRepositoryPort.findByEmail(email);
        if (userFromDB == null) {
            return false;
        }
        String hash = CaptchaServise.getRandomString(HASH_LENGHT);
        userFromDB.setCode(hash);
        userRepositoryPort.save(userFromDB);

        final String port = environment.getProperty("local.server.port");
        final String hostName = InetAddress.getLoopbackAddress().getHostName();

        StringBuilder text = new StringBuilder("Для восстановления пароля перейдите по ссылочке: ");
        text.append("http://")
                .append(hostName)
                .append(":")
                .append(port)
                .append("/api/auth/login/change-password/")
                .append(hash);

        emailService.sendSimpleMessage(email,"Восстановление пароля", text.toString());
        return true;
    }

    public ResponseEntity<ResultResponse> updateProfile(ProfileDto profile, HttpServletRequest request) {
        HashMap<String, Object> errors = new HashMap<>();
        User user = getCurrentUser(request);

        String email = profile.getEmail();
        if(!user.getEmail().equals(email) && email!=null){
            User userFromDB = userRepositoryPort.findUserByEmail(email);
            if (userFromDB != null) {
                errors.put("email", "Этот адрес уже зарегистрирован.");
            } else {
                user.setEmail(email);
            }
        }
        System.out.println(profile.isRemovePhoto());
        if(profile.isRemovePhoto()){
            if(user.getPhoto() != null){
                System.out.println(user.getPhoto());
                boolean result = storageService.delete(user.getPhoto());
                System.out.println(result);
                user.setPhoto(null);
            }
        } else {
            String photo = profile.getPhoto();
            if (photo != null) {
                if (!photo.contains("/img/upload/")) {
                    errors.put("photo", photo);
                } else if (!photo.isBlank() && !photo.equals(user.getPhoto())) {
                    user.setPhoto(photo);
                }
            }
        }

        String password = profile.getPassword();
        if(password!=null) {
            if (password.length() < 6) {
                errors.put("password", "Пароль короче 6 символов");
            } else {
                user.setPassword(passwordEncoder.encode(password));
            }
        }

        String name = profile.getName();
        if(name!=null) {
            if (name.length() < 4) {
                errors.put("name", "Имя указано неверно");
            } else {
                user.setName(name);
            }
        }

        userRepositoryPort.save(user);

        if(!errors.isEmpty()){
            return  new ResponseEntity<>(new ResultResponse(false,errors), HttpStatus.BAD_REQUEST);
        } else {
            return  new ResponseEntity<>(new ResultResponse(true, null), HttpStatus.OK);
        }
    }

    public LoggedInUserDto getLoggedInUser(User user) {
        return dtoConverter.userToLoggedInUser(user);
    }
}
