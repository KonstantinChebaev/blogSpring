package main.domain.post.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostWithCommentsDto {
    private Integer id;
    private LocalDateTime time;
    private PostUserDto user;
    private String title;
    private String text;
    private Long likeCount;
    private Long dislikeCount;
    private Integer viewCount;
    private List<CommentDto> comments;
    private List<String> tags;
}
