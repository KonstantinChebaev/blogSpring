package main.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModerationRequestDto {
    @JsonProperty(value = "post_id")
    private Integer postId;
    private String decision;
}
