package main.web.api;

import main.domain.ResultResponse;
import main.domain.TagNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class NotFoundExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TagNotFoundException.class)
    protected ResponseEntity<ResultResponse> handleThereIsNoSuchUserException() {
        return new ResponseEntity<>(ResultResponse.getBadResultResponse("error", "tag not found"), HttpStatus.NOT_FOUND);
    }
}