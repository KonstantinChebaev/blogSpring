package main.domain.post.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllPostsResponseDto {
    private int count;
    private List<PostPlainDto> posts;
}
