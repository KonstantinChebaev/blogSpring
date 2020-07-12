package main.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostPlainDto {
    private Integer id;
    @JsonFormat(pattern = "hh:mm dd.MM.yyyy")
    private LocalDateTime time;
    private PostUserDto user;
    private String title;
    private String announce;
    private Long likeCount;
    private Long dislikeCount;
    private Integer commentCount;
    private Integer viewCount;
}
