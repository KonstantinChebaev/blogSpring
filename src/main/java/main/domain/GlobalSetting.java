package main.domain;
//страница 98 (123 из 626) spring in action

import lombok.Data;

import javax.persistence.*;
@Data
@Entity
@Table(name="global_settings")
public class GlobalSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String value;

    public GlobalSetting (){

    }
}
