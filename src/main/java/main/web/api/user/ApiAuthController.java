package main.web.api.user;

import main.dao.UserRepository;
import main.domain.globallSettings.SettingsService;
import main.security.CaptchaServise;
import main.domain.ResultResponse;
import main.domain.user.*;
import main.domain.user.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/auth/")
public class ApiAuthController {

    private UserServise userServise;
    private UserRepository userRepository;
    private CaptchaServise captchaServise;
    private SettingsService settingsService;

    public ApiAuthController(UserServise userServise, UserRepository userRepository,
                             CaptchaServise captchaServise, SettingsService settingsService){
        this.userServise = userServise;
        this.userRepository = userRepository;
        this.captchaServise = captchaServise;
        this.settingsService = settingsService;
    }


    @PostMapping(value = "register")
    public ResponseEntity<?> apiAuthRegister(@RequestBody UserRegisterDto ur) {
        if(!settingsService.getSettings().getMultiuserMode()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return userServise.registerUser(ur);
    }

    @PostMapping(value = "login")
    public UserAuthResponceDto apiAuthLogin(@RequestBody UserLoginDto ul
                                            , HttpServletRequest request) {
        return userServise.loginUser(ul.getEmail(), ul.getPassword(), request);
    }

    @GetMapping(value = "check")
    public ResponseEntity<UserAuthResponceDto> apiAuthCheck(HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            User user = userRepository.findByEmail(emailUser).get();
            return new ResponseEntity<>(new UserAuthResponceDto(true, userServise.getLoggedInUser(user)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new UserAuthResponceDto(false, null), HttpStatus.OK);
        }
    }

    @GetMapping("logout")
    public Boolean logout(HttpServletRequest request, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return true;
    }

    // https://mailtrap.io/inboxes
    @PostMapping("/restore")
    public ResponseEntity<Boolean> restore(@RequestParam String email) {
        boolean result = userServise.restoreUserPassword(email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //need tests
    @PostMapping("/password")
    public ResponseEntity<ResultResponse> resetPassword(@RequestBody PasswordResetRequestDto request) {
        return new ResponseEntity<>(userServise.resetUserPassword(request), HttpStatus.OK);
    }

    @GetMapping(value = "/captcha")
    public ResponseEntity<CaptchaResponseDto> apiAuthCaptcha() {
        return new ResponseEntity<>(captchaServise.getCaptchaResponse(), HttpStatus.OK);
    }

}
