package main.web.api;

import main.domain.ResultResponse;
import main.domain.comment.CommentUseCase;
import main.domain.comment.NewCommentRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/comment")
public class ApiCommentController {

    @Autowired
    CommentUseCase commentUseCase;

    @PostMapping ("")
    public ResponseEntity<ResultResponse> getAllPosts(@RequestBody NewCommentRequestDto newCommentRequestDto,
                                                      HttpServletRequest request) {
        return commentUseCase.createComment(newCommentRequestDto, request);

    }
}
