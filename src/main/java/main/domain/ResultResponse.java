package main.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    private boolean result;
    private Map<String, Object> errors;

    public static ResultResponse getBadResultResponse(String key, String value){
        Map<String, Object> errs = new HashMap<>();
        errs.put(key, value);
        return new ResultResponse(false, errs);
    }
    public void addErrors(String key, String value){
        result = false;
        errors.put(key,value);
    }
    public void addErrors(HashMap <String, Object>errors){
        result = false;
        errors.putAll(errors);
    }
}
