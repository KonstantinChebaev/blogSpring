package main.domain.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TagResponseDto {
    List<TagDto> tags;

    public TagResponseDto(){
        tags = new ArrayList<>();
    }

    public void addTagToList(String name, Double weight){
        tags.add(new TagDto(name,weight));
    }

    @Data
    @NoArgsConstructor
    public class TagDto {
        String name;
        Double weight;
        public TagDto (String name, Double weight){
            this.name = name;
            this.weight = weight;
        }

    }

}
