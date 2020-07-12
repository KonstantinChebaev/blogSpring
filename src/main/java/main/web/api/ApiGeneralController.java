package main.web.api;


import main.domain.*;
import main.domain.globallSettings.GSettingsDto;
import main.domain.globallSettings.SettingsService;
import main.domain.post.PostServise;
import main.domain.tag.TagServise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    @Autowired
    TagServise tuc;

    @Autowired
    PostServise puc;

    @Autowired
    SettingsService settingsService;

    @Autowired
    StatisticServise statisticsServise;

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

    @GetMapping("/settings")
    public ResponseEntity<GSettingsDto> getSettings() {
        return new ResponseEntity<>(settingsService.getSettings(), HttpStatus.OK);
    }

    @PutMapping("/settings")
    public ResponseEntity<Boolean> updateSettings(@RequestBody GSettingsDto settings) {
        boolean result = settingsService.putSettings(settings);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //need tests
    @GetMapping("/statistics/{statisticsType}")
    public ResponseEntity<StatisticsDto> getStatistics(@PathVariable String statisticsType, HttpServletRequest request) {
        return new ResponseEntity<>(statisticsServise.getStatistics(statisticsType,request), HttpStatus.OK);
    }


}
