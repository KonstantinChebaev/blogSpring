package main.domain.comment;

import main.dao.CommentRepository;
import main.domain.ResultResponse;
import main.domain.post.Post;
import main.domain.post.PostRepositoryPort;
import main.domain.user.User;
import main.domain.user.UserServise;
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
    UserServise userAuthUseCase;

    @Autowired
    private CommentRepository commentsRepository;

    public ResponseEntity<?> createComment (NewCommentRequestDto newCommentRequestDto,
                                                         HttpServletRequest request){

        User user = userAuthUseCase.getCurrentUser(request);
        Optional<Post> optionalPost = postRepositoryPort.findById(newCommentRequestDto.getPostId());
        if(optionalPost.isEmpty()){
            return new ResponseEntity<>(ResultResponse.getBadResultResponse("not_found", "Пост не найден"), HttpStatus.BAD_REQUEST);
        }
        if (newCommentRequestDto.getText().length()<6){
            return new ResponseEntity<>(ResultResponse.getBadResultResponse("text", "Текст комментария не задан или слишком короткий"), HttpStatus.BAD_REQUEST);
        }

        PostComment parentComment = null;
        if(newCommentRequestDto.getParentId()!=null){
            Optional<PostComment> optionalPostComment = commentsRepository.findById(newCommentRequestDto.getParentId());
            if(optionalPostComment.isPresent()){
                parentComment = optionalPostComment.get();
            }
        }
        PostComment newPostComment = PostComment.builder()
                .parentPostComment(parentComment)
                .post(optionalPost.get())
                .user(user)
                .time(LocalDateTime.now())
                .text(newCommentRequestDto.getText())
                .build();
        newPostComment = commentsRepository.save(newPostComment);
        return new ResponseEntity<>(newPostComment.getId(), HttpStatus.OK);
    }


}
