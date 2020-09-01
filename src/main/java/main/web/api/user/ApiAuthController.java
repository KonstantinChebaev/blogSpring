package main.web.api.user;

import main.security.CaptchaServise;
import main.domain.ResultResponse;
import main.domain.user.*;
import main.domain.user.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/api/auth/")
public class ApiAuthController {
    @Autowired
    UserServise userServise;

    @Autowired
    CaptchaServise captchaServise;

    @PostMapping(value = "register")
    public ResponseEntity<?> apiAuthRegister(@RequestBody UserRegisterDto ur) {
        return userServise.registerUser(ur);
    }


    //сделать еще так чтобы, при авторизации в течении одной сессии нескольких разных
    //пользователей одного за другим, предыдущий разлогинивался а авторизованным оставался последний
    @PostMapping(value = "login")
    public UserAuthResponceDto apiAuthLogin(@RequestBody UserLoginDto ul
                                            , HttpServletRequest request) {
        return userServise.loginUser(ul.getEmail(), ul.getPassword(), request);
    }

    @GetMapping(value = "check")
    public HashMap<String, Object> apiAuthCheck(HttpServletRequest request) {
        HashMap<String, Object> responce = new HashMap<>();
        User user = userServise.getCurrentUser(request);
        if (user == null) {
            responce.put("result", "false");
        } else {
            responce.put("result", "true");
            responce.put("user", userServise.getLoggedInUser(user));
        }
        return responce;
    }

    @GetMapping("logout")
    public HashMap<String, Object> logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("result", "true");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return responseMap;
    }

    // https://mailtrap.io/inboxes
    @PostMapping("/restore")
    public ResponseEntity<HashMap<String, Boolean>> restore(@RequestParam String email) {
        boolean result = userServise.restoreUserPassword(email);
        HashMap<String,Boolean> response = new HashMap<>();
        response.put("result", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //need tests
    @PostMapping("/password")
    public ResponseEntity<ResultResponse> resetPassword(@RequestBody PasswordResetRequestDto request) {
        return new ResponseEntity<>(userServise.resetUserPassword(request), HttpStatus.OK);
    }

    //need tests
    @GetMapping(value = "/captcha")
    public ResponseEntity<CaptchaResponseDto> apiAuthCaptcha() {
        return new ResponseEntity<>(captchaServise.getCaptchaResponse(), HttpStatus.OK);
    }

}
