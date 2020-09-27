package main.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarResponseDto {
    @JsonProperty("years")
    private Set<Integer> allYears;
    private Map<String, Long> posts;
}
