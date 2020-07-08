package main.web.api.user;

import main.domain.ResultResponse;
import main.domain.user.*;
import main.domain.user.dto.UserAuthResponceDto;
import main.domain.user.dto.UserLoginDto;
import main.domain.user.dto.UserRegisterDto;
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
    UserAuthServise userServise;

    @PostMapping(value = "register")
    public UserAuthResponceDto apiAuthRegister(@RequestBody UserRegisterDto ur) {
        return userServise.registerUser(ur);
    }

    @PostMapping(value = "login")
    public UserAuthResponceDto apiAuthLogin (@RequestBody UserLoginDto ul) {
        return userServise.loginUser(ul.getEmail(), ul.getPassword());
    }

    @GetMapping(value = "check")
    public HashMap<String,Object> apiAuthCheck (HttpServletRequest request) {
        HashMap<String,Object> responce = new HashMap<>();
        User user = userServise.getCurrentUser(request);
        if(user == null){
            responce.put("result","false");
        } else {
            responce.put("result","true");
            responce.put("user",user);
        }
        return responce;
    }

    @GetMapping("logout")
    public HashMap<String,Object> logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        HashMap<String,Object> responseMap = new HashMap<>();
        responseMap.put("result","true");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return responseMap;
    }

    // https://mailtrap.io/inboxes
    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restore(@RequestParam String email) {
        ResultResponse response = new ResultResponse();
        response.setResult(userServise.restoreUserPassword(email));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
