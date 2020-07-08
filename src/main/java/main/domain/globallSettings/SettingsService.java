package main.domain.globallSettings;

import main.dao.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsService {
    private static final int MULTIUSER_MODE_ID = 1;
    private static final int POST_PREMODERATION_ID = 2;
    private static final int STATISTICS_IS_PUBLIC_ID = 3;

    @Autowired
    private GlobalSettingsRepository settingsRepository;


    public GSettingsDto getSettings() {
        GSettingsDto settings = new GSettingsDto();
        Iterable <GlobalSetting> settingsFromDB = settingsRepository.findAll();
        settingsFromDB.forEach(gs -> {
            if(gs.getId() == MULTIUSER_MODE_ID){
                settings.setMultiuserMode(gs.getValue().equals("YES"));
            } else if (gs.getId() == POST_PREMODERATION_ID){
                settings.setPostPremoderation(gs.getValue().equals("YES"));
            } else if (gs.getId() == STATISTICS_IS_PUBLIC_ID){
                settings.setStatisticsIsPublic(gs.getValue().equals("YES"));
            }
        });
        return settings;
    }

    public boolean putSettings(GSettingsDto settings) {
        Iterable <GlobalSetting> settingsFromDB = settingsRepository.findAll();
        settingsFromDB.forEach(gs -> {
            if(gs.getId() == MULTIUSER_MODE_ID){
                if(settings.getMultiuserMode()){
                    gs.setValue("YES");
                }else {
                    gs.setValue("NO");
                };
            } else if (gs.getId() == POST_PREMODERATION_ID){
                if(settings.getPostPremoderation()){
                    gs.setValue("YES");
                }else {
                    gs.setValue("NO");
                };
            } else if (gs.getId() == STATISTICS_IS_PUBLIC_ID){
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

    @PostConstruct
    private void cteateDefaultSettings (){
        List<GlobalSetting> gsList = new ArrayList<GlobalSetting>();
        gsList.add(new GlobalSetting(MULTIUSER_MODE_ID, "MULTIUSER_MODE", "Многопользовательский режим", "NO"));
        gsList.add(new GlobalSetting(POST_PREMODERATION_ID, "POST_PREMODERATION", "Премодерация постов", "NO"));
        gsList.add(new GlobalSetting(STATISTICS_IS_PUBLIC_ID, "STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "NO"));
        settingsRepository.saveAll(gsList);
    }



}
