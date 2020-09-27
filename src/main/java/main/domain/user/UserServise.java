package main.domain.user;

import main.dao.UserRepository;
import main.security.CaptchaServise;
import main.domain.DtoConverter;
import main.domain.ResultResponse;
import main.domain.StorageService;
import main.domain.user.dto.*;
import main.security.EmailService;
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
import java.util.Optional;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Component
public class UserServise {
    private static final int HASH_LENGHT = 16;

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private EmailService emailService;
    private CaptchaServise captchaServise;
    private DtoConverter dtoConverter;
    private Environment environment;
    private StorageService storageService;
    private AuthenticationManager authenticationManager;

    public UserServise(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       EmailService emailService,
                       CaptchaServise captchaServise,
                       DtoConverter dtoConverter,
                       Environment environment,
                       StorageService storageService,
                       AuthenticationManager authenticationManager){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.captchaServise = captchaServise;
        this.dtoConverter = dtoConverter;
        this.environment = environment;
        this.storageService = storageService;
        this.authenticationManager = authenticationManager;
    }



    public ResponseEntity<?> registerUser(UserRegisterDto urd){
        HashMap <String, Object> errors = new HashMap<>();
        if (userRepository.findByEmail(urd.getEmail()).isPresent()) {
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
        userRepository.save(newUser);
        LoggedInUserDto loggedInUserDto = dtoConverter.userToLoggedInUser(newUser);
        return new ResponseEntity<>(new UserAuthResponceDto(true, loggedInUserDto), HttpStatus.OK);
    }

    public ResultResponse resetUserPassword(PasswordResetRequestDto request) {
        HashMap <String, Object> errors = new HashMap<>();
        Optional<User> optionalUser = userRepository.findByCode(request.getCode());
        if (optionalUser.isEmpty()) {
            errors.put("code", "Ссылка для восстановления пароля устарела.\n <a href=/auth/restore>Запросить ссылку снова</a>");
        }
        errors.putAll(validateUserInputAndGetErrors(request.getPassword(),request.getCaptcha(),request.getCaptchaSecret()));
        if(!errors.isEmpty()){
            return new ResultResponse(false, errors);
        }
        User userFromDB = optionalUser.get();
        userFromDB.setCode(null);
        userFromDB.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(userFromDB);
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
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            return new UserAuthResponceDto(false,null);
        }
        User user = optionalUser.get();
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

    public boolean restoreUserPassword(String email) {
        Optional<User> optionalUser= userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User userFromDB = optionalUser.get();
        String hash = CaptchaServise.getRandomString(HASH_LENGHT);
        userFromDB.setCode(hash);
        userRepository.save(userFromDB);

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

    public ResponseEntity<ResultResponse> updateProfile(ProfileDto profile, String userEmail) {
        HashMap<String, Object> errors = new HashMap<>();
        User user = userRepository.findByEmail(userEmail).get();

        String email = profile.getEmail();
        if(!user.getEmail().equals(email) && email!=null){
            if (userRepository.findByEmail(email).isPresent()) {
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
                if (!photo.contains("/upload/")) {
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

        userRepository.save(user);

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
