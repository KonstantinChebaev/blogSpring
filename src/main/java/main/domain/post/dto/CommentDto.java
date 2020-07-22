package main.domain.post.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Integer id;
    private LocalDateTime time;
    private UserWithPhotoDto user;
    private String text;
}
