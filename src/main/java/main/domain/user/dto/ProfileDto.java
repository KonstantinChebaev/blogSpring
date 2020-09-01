package main.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    public String photo;
    public boolean removePhoto;
    public String name;
    public String email;
    public String password;
}
