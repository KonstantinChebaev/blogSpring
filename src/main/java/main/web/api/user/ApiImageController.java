package main.web.api.user;

import main.domain.ResultResponse;
import main.domain.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

@RestController
public class ApiImageController {

    @Autowired
    private StorageService storageService;

    @GetMapping(value = "/img/upload/{dir1}/{dir2}/{fileName}")
    public @ResponseBody
    byte[] getImage(@PathVariable String dir1,
                    @PathVariable String dir2,
                    @PathVariable String fileName) {
        String route = new File("").getAbsolutePath()
                .concat("/src/main/resources/static/img/upload/" + dir1 + "/" + dir2 + "/" + fileName);
        return storageService.getImageByPath(Path.of(route));
    }

    @PostMapping(value = "/api/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> postImage(@RequestParam("image") MultipartFile image) {
        String response = storageService.store(image);
        if (!response.contains("/img/upload/")){
            String finalResponce = "{  \"result\": false, \"errors\": { \"image\": \""+response+"\" } }";
            return new ResponseEntity<>(finalResponce, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
