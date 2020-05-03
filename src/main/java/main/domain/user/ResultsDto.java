package main.domain.user;

import lombok.Data;

@Data
public class ResultsDto {
    private boolean result;
    public ResultsDto(boolean result){
        this.result = result;

    }


}
