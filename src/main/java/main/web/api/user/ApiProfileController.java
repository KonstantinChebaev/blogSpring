package main.web.api.user;


import main.domain.ResultResponse;
import main.domain.user.UserServise;
import main.domain.user.dto.ProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
public class ApiProfileController {

    @Autowired
    UserServise userServise;


    @PostMapping(value = "api/profile/my")
    public ResponseEntity<ResultResponse> updateProfile(@RequestBody ProfileDto profile, HttpServletRequest request) {
        return userServise.updateProfile(profile, request);
    }


}
