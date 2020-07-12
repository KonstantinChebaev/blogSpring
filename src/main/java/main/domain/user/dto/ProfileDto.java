package main.domain.user.dto;

import lombok.Data;

@Data
public class ProfileDto {
    public String photo;
    public int removePhoto;
    public String name;
    public String email;
    public String password;
}
