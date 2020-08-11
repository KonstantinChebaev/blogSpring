package main.security;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name="captcha_codes")
public class CaptchaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime time;
    @Column(nullable = false, columnDefinition = "TINYTEXT")
    private String code;
    @Column(name="secret_code", nullable = false, columnDefinition = "TINYTEXT")
    private String secretCode;

}
