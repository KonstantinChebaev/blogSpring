package main.web.api;


import main.domain.CalendarResponseDto;
import main.domain.GeneralInfo;
import main.domain.ModerationRequestDto;
import main.domain.post.PostUseCase;
import main.domain.tag.TagUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    @Autowired
    TagUseCase tuc;

    @Autowired
    PostUseCase puc;

    @GetMapping("/init")
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

    //need tests
    @GetMapping("/tags")
    public HashMap<String, Object> getTags (@RequestParam String query){
        return tuc.getTagsWeights(query);
    }

    //need tests
    @PostMapping("/moderation")
    public ResponseEntity<?> postModeration(@RequestBody ModerationRequestDto moderationRequestDto,
                                         HttpServletRequest request) {
        return puc.moderate(moderationRequestDto, request);
    }

    //need tests
    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponseDto> getCalendar(
            @RequestParam(required = false) String year) {
        return puc.getCalend(year);
    }
}
