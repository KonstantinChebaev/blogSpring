package main.security;


import main.config.EmailCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    @Autowired
    EmailCfg emailCfg;

    public boolean sendEmail (String email, String text){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailCfg.getHost());
        mailSender.setPort(emailCfg.getPort());
        mailSender.setUsername(emailCfg.getUsername());
        mailSender.setPassword(emailCfg.getPassword());

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailCfg.getUsername());
        mailMessage.setTo(email);
        mailMessage.setSubject("Восстановление пароля");
        mailMessage.setText(text);

        mailSender.send(mailMessage);

        return true;

    }


}
