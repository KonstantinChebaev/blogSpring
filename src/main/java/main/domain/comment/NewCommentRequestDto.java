package main.domain.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NewCommentRequestDto {
    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonProperty("post_id")
    private Integer postId;

    private String text;
}
