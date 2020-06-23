package main.domain.comment;

import main.dao.CommentRepository;
import main.domain.ResultResponse;
import main.domain.post.Post;
import main.domain.post.PostRepositoryPort;
import main.domain.user.User;
import main.domain.user.UserAuthUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Component
public class CommentUseCase {

    @Autowired
    PostRepositoryPort postRepositoryPort;

    @Autowired
    UserAuthUseCase userAuthUseCase;

    @Autowired
    private CommentRepository commentsRepository;

    public ResponseEntity<ResultResponse> createComment (NewCommentRequestDto newCommentRequestDto,
                                                         HttpServletRequest request){
        ResultResponse response = new ResultResponse();
        User user = userAuthUseCase.getCurrentUser(request);
        Optional<Post> optionalPost = postRepositoryPort.findById(newCommentRequestDto.getPostId());
        Optional<PostComment> optionalPostComment = commentsRepository.findById(newCommentRequestDto.getParentId());
        if(optionalPostComment.isEmpty()||optionalPost.isEmpty()){
            response.setResult(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (newCommentRequestDto.getText().length()<6){
            response.setResult(false);
            HashMap<String, Object> errors = new HashMap<>();
            errors.put("text", "Текст комментария не задан или слишком короткий");
            response.setErrors(errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        final PostComment newPostComment = PostComment.builder()
                .parentPostComment(optionalPostComment.get())
                .post(optionalPost.get())
                .user(user)
                .time(LocalDateTime.now())
                .text(newCommentRequestDto.getText())
                .build();
        commentsRepository.save(newPostComment);
        return new ResponseEntity<>(response, HttpStatus.OK);


    }


}
