package main.domain.globallSettings;
//страница 98 (123 из 626) spring in action

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Data
@Entity
@Table(name="global_settings")
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 3, nullable = false)
    private String value;

    public GlobalSetting (String code, String name, String value){
        this.code = code;
        this.name = name;
        this.value = value;

    }

}
