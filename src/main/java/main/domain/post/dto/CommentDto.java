package main.domain.post.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CommentDto {
    private Integer id;
    private long timestamp;
    private UserWithPhotoDto user;
    private String text;
}
