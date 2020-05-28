package main.web.api;


import main.domain.GeneralInfo;
import main.domain.tag.TagUseCase;
import main.domain.tag.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ApiGeneralController {

    @Autowired
    TagUseCase tuc;

    @GetMapping("/api/init")
    public GeneralInfo getGeneral (){
        GeneralInfo gi = GeneralInfo.builder()
                .title("DevPub")
                .subtitle("200-10-10")
                .phone("+7 903 666-44-55")
                .email("mail@mail.ru")
                .copyright("Дмитрий Сергеев")
                .copyrightForm("2005")
                .build();
        return gi;
    }

    @GetMapping("/api/tags")
    public List<Tags> getTags (@RequestParam String query){
        return tuc.getQueryTags(query);
    }
}
