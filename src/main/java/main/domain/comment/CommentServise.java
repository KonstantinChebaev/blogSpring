package main.domain.comment;

import main.dao.CommentRepository;
import main.domain.ResultResponse;
import main.domain.post.Post;
import main.domain.post.PostRepositoryPort;
import main.domain.user.User;
import main.domain.user.UserRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CommentServise {

    private PostRepositoryPort postRepositoryPort;
    private CommentRepository commentsRepository;
    private UserRepositoryPort userRepositoryPort;

    public CommentServise (PostRepositoryPort postRepositoryPort,CommentRepository commentsRepository,
                           UserRepositoryPort userRepositoryPort){
        this.postRepositoryPort =  postRepositoryPort;
        this.commentsRepository = commentsRepository;
        this.userRepositoryPort = userRepositoryPort;
    }

    public ResponseEntity<?> createComment (NewCommentRequestDto newCommentRequestDto, String userEmail){

        User user = userRepositoryPort.findByEmail(userEmail);
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
