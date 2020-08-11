package main.dao;

import main.security.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CaptchaCodeRepository extends CrudRepository<CaptchaCode, Long> {

    @Modifying
  //  @Query("DELETE FROM captcha_codes WHERE time < ':localDateTime' ")
    void deleteByTime(LocalDateTime localDateTime);

    CaptchaCode findBySecretCode(String secretCode);
}