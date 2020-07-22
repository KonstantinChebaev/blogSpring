package main.domain.user;

import main.domain.CaptchaServise;
import main.domain.ResultResponse;
import main.domain.StorageService;
import main.domain.user.dto.PasswordResetRequestDto;
import main.domain.user.dto.ProfileDto;
import main.domain.user.dto.UserAuthResponceDto;
import main.domain.user.dto.UserRegisterDto;
import main.security.EmailService;
import main.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.HashMap;

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
    private StorageService storageService;

    @Autowired
    private Environment environment;

//    @PostConstruct
//    private void cteateDefaultUser (){
//        User user = User.builder()
//                .email("user@user.com")
//                .isModerator(true)
//                .name("user")
//                .password(passwordEncoder.encode("user@user.com"))
//                .regTime(LocalDateTime.now())
//                .id(1)
//                .build();
//        userRepositoryPort.save(user);
//    }



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
        return new ResponseEntity<>(new UserAuthResponceDto(true, newUser), HttpStatus.OK);
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
        if(request.isRequestedSessionIdValid() && request.getUserPrincipal()!=null){
            String principalName = request.getUserPrincipal().getName();
            String email = principalName.substring(principalName.indexOf(" email=")+7,principalName.indexOf(", password="));
            return userRepositoryPort.findByEmail(email);
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
        text
                .append("http://")
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
        User userFromDB = userRepositoryPort.findUserByEmail(email);
        if (userFromDB != null) {
            errors.put("email", "Этот адрес уже зарегистрирован.");
        } else {
            user.setEmail(email);
        }

        boolean removePhoto = profile.getRemovePhoto() == 1;
        String photo = profile.getPhoto();
        if (removePhoto && (user.getPhoto() != null)) {
            storageService.delete(user.getPhoto());
            user.setPhoto(null);
        } else if (photo != null && (!photo.isBlank() && !photo.equals(user.getPhoto()))) {
            user.setPhoto(photo);
        }

        String password = profile.getPassword();
        if (password == null || password.length() < 6) {
            errors.put("password", "Пароль короче 6 символов");
        } else {
            user.setPassword(passwordEncoder.encode(password));
        }

        String name = profile.getName();
        if (password == null || name.length() < 4) {
            errors.put("name", "Пароль короче 4 символов");
        } else {
            user.setName(name);
        }
        if(!errors.isEmpty()){
            return  new ResponseEntity<>(new ResultResponse(false,errors), HttpStatus.BAD_REQUEST);
        } else {
            return  new ResponseEntity<>(new ResultResponse(true, null), HttpStatus.OK);
        }
    }
}