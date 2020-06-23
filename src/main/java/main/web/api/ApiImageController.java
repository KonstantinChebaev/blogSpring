package main.web.api;

import main.domain.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;

@Controller
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
    public String postImage(@RequestParam("image") MultipartFile image) {
        String answer = storageService.store(image);
        return answer;
    }
}
