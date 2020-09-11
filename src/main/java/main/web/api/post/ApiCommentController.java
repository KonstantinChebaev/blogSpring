package main.web.api.post;

import main.domain.comment.CommentServise;
import main.domain.comment.NewCommentRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/comment")
public class ApiCommentController {

    @Autowired
    CommentServise commentServise;

    @PostMapping ("")
    public ResponseEntity<?> createNewComment(@RequestBody NewCommentRequestDto newCommentRequestDto,
                                                      HttpServletRequest request) {
        if (request.isRequestedSessionIdValid() && request.getUserPrincipal() != null) {
            String emailUser = request.getUserPrincipal().getName();
            return commentServise.createComment(newCommentRequestDto, emailUser);
        } else {
            return new ResponseEntity<>(null,HttpStatus.FORBIDDEN);
        }


    }
}
