package main.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.domain.user.User;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAuthResponceDto {
    private boolean result;
    private LoggedInUserDto user;
}
