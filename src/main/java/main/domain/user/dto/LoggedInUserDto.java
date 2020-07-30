package main.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoggedInUserDto {
    private int id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}
