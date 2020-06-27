package main.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarResponseDto {
    private Set<Integer> allYears;
    private Map<String, Long> posts;
}
