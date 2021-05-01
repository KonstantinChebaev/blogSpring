package main.web.api;


import main.domain.*;
import main.domain.globallSettings.GSettingsDto;
import main.domain.globallSettings.SettingsService;
import main.domain.post.PostServise;
import main.domain.tag.TagResponseDto;
import main.domain.tag.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private TagService tagService;
    private PostServise postServise;
    private SettingsService settingsService;
    private StatisticServise statisticsServise;

    public ApiGeneralController(TagService tagService,
                                PostServise postServise,
                                SettingsService settingsService,
                                StatisticServise statisticsServise) {
        this.tagService = tagService;
        this.postServise = postServise;
        this.settingsService = settingsService;
        this.statisticsServise = statisticsServise;
    }


    @GetMapping("/init")
    public GeneralInfo getGeneral() {
        GeneralInfo gi = GeneralInfo.builder()
                .title("DevPub")
                .subtitle("01.05.2021")
                .phone("+7 952 215-68-42")
                .email("konstantin.chebaev@gmail.com")
                .copyright("Константин Чебаев")
                .copyrightForm("2020")
                .build();
        return gi;
    }


    @GetMapping("/tag")
    public TagResponseDto getTags(@RequestParam(value = "query", required = false) String query) {
        return tagService.getTagsWeights(query);
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponseDto> getCalendar(
            @RequestParam(required = false) String year) {
        return postServise.getCalend(year);
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

    @GetMapping("/statistics/{statisticsType}")
    public ResponseEntity<StatisticsDto> getStatistics(@PathVariable String statisticsType, HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            GSettingsDto settings = settingsService.getSettings();
            return statisticsServise.getStatistics(statisticsType, emailUser, settings.getStatisticsIsPublic());
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
}
