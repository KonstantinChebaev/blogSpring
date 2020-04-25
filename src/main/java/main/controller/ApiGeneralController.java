package main.controller;


import main.model.GeneralInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGeneralController {

    @GetMapping("/api/init/")
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
}
