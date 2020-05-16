package main.domain.post;

import lombok.Data;

@Data
public class PostPostDto {
    private String time;
    private byte active;
    private String title;
    private String text;
    private String tags;

}
