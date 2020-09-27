package main.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.domain.user.User;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAuthResponceDto {
    private boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LoggedInUserDto user;
}
