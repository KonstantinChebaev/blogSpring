package main.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class GeneralInfo {
    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightForm;


}
