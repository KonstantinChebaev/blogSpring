package main.security;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.config.CaptchaCfg;
import main.dao.CaptchaCodeRepository;
import main.domain.user.dto.CaptchaResponseDto;
import main.security.CaptchaCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class CaptchaServise {

    @Autowired
    CaptchaCodeRepository captchaCodeRepository;

    private static final Cage CAGE = new GCage();
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    public static String getRandomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }


    public CaptchaResponseDto getCaptchaResponse () {
        deleteOutdatedCaptchas(CaptchaCfg.CAPTCHA_UPDATE_HOURS);
        CaptchaCode cc = createCaptchaEntity();
        byte[] fileContent = CAGE.draw(cc.getCode());
        String encodedString = "data:image/png;base64, " + Base64.getEncoder().encodeToString(fileContent);
        return new CaptchaResponseDto(cc.getSecretCode(),encodedString);
    }


    private CaptchaCode createCaptchaEntity (){
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setTime(LocalDateTime.now());
        captchaCode.setCode(getRandomString(CaptchaCfg.CAPTCHA_CODE_LENGTH));
        captchaCode.setSecretCode(getRandomString(16));
        return captchaCodeRepository.save(captchaCode);
    }

    private void deleteOutdatedCaptchas(Integer hoursToBeUpdated) {
        final LocalDateTime expirationTime = LocalDateTime.now().minusHours(hoursToBeUpdated);
        captchaCodeRepository.deleteByTime(expirationTime);
    }

    public boolean isValidCaptcha(String captcha, String captchaSecretCode) {
            CaptchaCode cc = captchaCodeRepository.findBySecretCode(captchaSecretCode);
            if (cc == null || !cc.getCode().equals(captcha)){
                return false;
            } else {
                return true;
            }
    }
}
