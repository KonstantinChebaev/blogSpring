package main.web.api.user;


import main.domain.ResultResponse;
import main.domain.StorageService;
import main.domain.user.User;
import main.domain.user.UserServise;
import main.domain.user.dto.ProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile/my")
public class ApiProfileController {

    @Autowired
    UserServise userServise;

    @Autowired
    StorageService storageService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultResponse> updateProfile(@RequestBody ProfileDto profile, HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            return userServise.updateProfile(profile, emailUser);
        } else {
            return new ResponseEntity<>(null,HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultResponse> updateProfileWithPhoto(@RequestParam("photo") MultipartFile photo,
                                                                 @RequestParam("removePhoto") boolean removePhoto,
                                                                 @RequestParam("name") String name,
                                                                 @RequestParam("email") String email,
                                                                 @RequestParam("password") String password,
                                                                    HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            String pathToSavedFile = storageService.storePhoto(photo);
            ProfileDto profile = ProfileDto.builder()
                    .photo(pathToSavedFile)
                    .removePhoto(removePhoto)
                    .name(name)
                    .email(email)
                    .password(password)
                    .build();
            return userServise.updateProfile(profile, emailUser);
        } else {
            return new ResponseEntity<>(null,HttpStatus.FORBIDDEN);
        }

    }






}
