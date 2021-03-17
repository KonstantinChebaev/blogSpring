package main.domain.globallSettings;

import main.dao.GlobalSettingsRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsService {

    private GlobalSettingsRepository settingsRepository;

    public SettingsService(GlobalSettingsRepository settingsRepository){
        this.settingsRepository = settingsRepository;
    }

    @PostConstruct
    private void cteateDefaultSettings (){
        if(settingsRepository.count()==0) {
            List<GlobalSetting> gsList = new ArrayList<GlobalSetting>();
            gsList.add(new GlobalSetting("MULTIUSER_MODE", "Многопользовательский режим", "YES"));
            gsList.add(new GlobalSetting("POST_PREMODERATION", "Премодерация постов", "YES"));
            gsList.add(new GlobalSetting("STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "NO"));
            settingsRepository.saveAll(gsList);
        }
    }


    public GSettingsDto getSettings() {
        GSettingsDto settings = new GSettingsDto();
        Iterable <GlobalSetting> settingsFromDB = settingsRepository.findAll();
        settingsFromDB.forEach(gs -> {
            if(gs.getCode().equals("MULTIUSER_MODE")){
                settings.setMultiuserMode(gs.getValue().equals("YES"));
            } else if (gs.getCode().equals("POST_PREMODERATION")){
                settings.setPostPremoderation(gs.getValue().equals("YES"));
            } else if (gs.getCode().equals("STATISTICS_IS_PUBLIC")){
                settings.setStatisticsIsPublic(gs.getValue().equals("YES"));
            }
        });
        return settings;
    }

    public boolean putSettings(GSettingsDto settings) {
        Iterable <GlobalSetting> settingsFromDB = settingsRepository.findAll();
        settingsFromDB.forEach(gs -> {
            if(gs.getCode().equals("MULTIUSER_MODE")){
                if(settings.getMultiuserMode()){
                    gs.setValue("YES");
                }else {
                    gs.setValue("NO");
                };
            } else if (gs.getCode().equals("POST_PREMODERATION")){
                if(settings.getPostPremoderation()){
                    gs.setValue("YES");
                }else {
                    gs.setValue("NO");
                };
            } else if (gs.getCode().equals("STATISTICS_IS_PUBLIC")){
                if(settings.getStatisticsIsPublic()){
                    gs.setValue("YES");
                }else {
                    gs.setValue("NO");
                };
            }
            settingsRepository.saveAll(settingsFromDB);
        });
        return true;
    }
}