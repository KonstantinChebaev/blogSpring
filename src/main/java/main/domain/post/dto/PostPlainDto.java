package main.domain.post.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PostPlainDto {
    private Integer id;
    private Long timestamp;
    private PostUserDto user;
    private String title;
    private String announce;
    private Long likeCount;
    private Long dislikeCount;
    private Integer commentCount;
    private Integer viewCount;
}
