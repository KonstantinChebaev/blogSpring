package main.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CaptchaCfg {
        public static final Integer CAPTCHA_CODE_LENGTH = 4;
        public static final Integer CAPTCHA_UPDATE_HOURS = 1;
}
