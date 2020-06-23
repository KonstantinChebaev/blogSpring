package main.domain;

import lombok.Data;

import java.util.Map;

@Data
public class ResultResponse {
    private Boolean result;
    private Map<String, Object> errors;
}
