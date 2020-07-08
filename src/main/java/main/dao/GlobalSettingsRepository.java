package main.dao;

import main.domain.globallSettings.GlobalSetting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSetting, Integer> {
    Optional <GlobalSetting> findByCode(String code);
}
