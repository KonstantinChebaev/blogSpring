package main.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PostPostDto {
    private long timestamp;
    private Boolean active;
    private String title;
    private String text;
    private Set<String> tags;

}
