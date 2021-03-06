package main;

import main.config.EmailCfg;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


@SpringBootApplication
@EnableConfigurationProperties(EmailCfg.class)
public class Main {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);

    }
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
