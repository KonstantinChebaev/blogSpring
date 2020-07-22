package main.domain.post.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserWithPhotoDto extends PostUserDto{
    private String photo;

    public UserWithPhotoDto(int id, String name, String photo) {
        super(id,name);
        this.photo = photo;
    }
}
